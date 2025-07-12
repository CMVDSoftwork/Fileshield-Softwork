package org.CMVD.Softwork.Fileshield.Servicios.Impl;

import org.CMVD.Softwork.Fileshield.DTO.SessionRequest.UsuarioDTO;
import org.CMVD.Softwork.Fileshield.Model.Usuario;
import org.CMVD.Softwork.Fileshield.Repositorios.UsuarioRepositorio;
import org.CMVD.Softwork.Fileshield.Security.JwtTokenProvider;
import org.CMVD.Softwork.Fileshield.Servicios.CifradorAESService;
import org.CMVD.Softwork.Fileshield.Servicios.UsuarioService;
import org.CMVD.Softwork.Fileshield.DTO.SessionRequest.LoginRequest;
import org.CMVD.Softwork.Fileshield.DTO.SessionRequest.LoginResponse;
import org.CMVD.Softwork.Fileshield.DTO.SessionRequest.RegistroRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepositorio RepoUsuario;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private CifradorAESService cifradorAES;
    @Autowired
    private PasswordEncryptorService passwordEncryptor;


    @Override
    public LoginResponse registrar(RegistroRequest request) {
        if (RepoUsuario.findByCorreo(request.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado.");
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(request.getNombre());
        nuevo.setApellidoP(request.getApellidoP());
        nuevo.setApellidoM(request.getApellidoM());
        nuevo.setCorreo(request.getCorreo());
        nuevo.setContrasena(passwordEncoder.encode(request.getContrasena()));

        SecretKey claveAES = cifradorAES.generarClave();
        byte[] claveBytes = cifradorAES.claveABytes(claveAES);

        String salt = KeyGenerators.string().generateKey();
        BytesEncryptor encryptor = Encryptors.stronger(request.getContrasena(), salt);
        byte[] claveCifradaBytes = encryptor.encrypt(claveBytes);

        String claveCifradaBase64 = java.util.Base64.getEncoder().encodeToString(claveCifradaBytes);
        String claveFinal = salt + ":" + claveCifradaBase64;
        nuevo.setClaveCifDesPersonal(claveFinal);

        Usuario guardado = RepoUsuario.save(nuevo);
        String token = jwtTokenProvider.generarToken(guardado);

        byte[] claveAESDescifradaBytes = encryptor.decrypt(claveCifradaBytes);
        String claveAESDescifradaParaClienteBase64 = Base64.getEncoder().encodeToString(claveAESDescifradaBytes);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setNombre(guardado.getNombre());
        response.setCorreo(guardado.getCorreo());
        response.setIdUsuario(guardado.getIdUsuario());
        response.setClaveCifDesPersonal(claveAESDescifradaParaClienteBase64);

        return response;
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = RepoUsuario.findByCorreo(request.getCorreo()).orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Contraseña incorrecta.");
        }

        String clavePersonalCifradaAlmacenada = usuario.getClaveCifDesPersonal();

        if (clavePersonalCifradaAlmacenada == null || !clavePersonalCifradaAlmacenada.contains(":")) {
            throw new RuntimeException("Clave personal cifrada no disponible o mal formada para el usuario.");
        }

        String[] partes = clavePersonalCifradaAlmacenada.split(":");
        String saltParaDescifrado = partes[0];
        String claveAESCifradaSoloBase64 = partes[1];

        byte[] claveAESCifradaBytes = Base64.getDecoder().decode(claveAESCifradaSoloBase64);

        BytesEncryptor desencriptadorClaveAES = Encryptors.stronger(request.getContrasena(), saltParaDescifrado);
        byte[] claveAESDescifradaBytes = desencriptadorClaveAES.decrypt(claveAESCifradaBytes); // Aquí obtienes los bytes de la CLAVE AES pura
        String claveAESDescifradaParaClienteBase64 = Base64.getEncoder().encodeToString(claveAESDescifradaBytes);

        String token = jwtTokenProvider.generarToken(usuario);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setNombre(usuario.getNombre());
        response.setCorreo(usuario.getCorreo());
        response.setIdUsuario(usuario.getIdUsuario());
        response.setClaveCifDesPersonal(claveAESDescifradaParaClienteBase64);

        return response;
    }

    @Override
    public SecretKey recuperarClaveAES(Integer idUsuario, String contrasena) {
        Usuario usuario = RepoUsuario.findById(idUsuario).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String cifrado = usuario.getClaveCifDesPersonal();

        if (cifrado == null || !cifrado.contains(":")) {
            throw new RuntimeException("Clave cifrada no disponible o mal formada");
        }

        String[] partes = cifrado.split(":");
        String salt = partes[0];
        String claveCifradaBase64 = partes[1];

        byte[] claveCifrada = Base64.getDecoder().decode(claveCifradaBase64);
        BytesEncryptor decryptor = Encryptors.stronger(contrasena, salt);

        try {
            byte[] claveBytes = decryptor.decrypt(claveCifrada);
            return new SecretKeySpec(claveBytes, "AES");
        } catch (Exception e) {
            throw new RuntimeException("No se pudo descifrar la clave AES. Contraseña incorrecta o clave corrupta.");
        }
    }

    @Override
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return RepoUsuario.findByCorreo(correo);
    }


    @Override
    public void cambiarContrasena(String correo, String contrasenaActual, String nuevaContrasena) {
        Usuario usuario = RepoUsuario.findByCorreo(correo).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }

        String claveCifradaBase64 = usuario.getClaveCifDesPersonal();
        if (claveCifradaBase64 == null || !claveCifradaBase64.contains(":")) {
            throw new RuntimeException("No se encontró una clave cifrada válida.");
        }

        String[] partes = claveCifradaBase64.split(":");
        String saltAnterior = partes[0];
        String claveAnteriorCifrada = partes[1];

        byte[] claveBytesCifrados = Base64.getDecoder().decode(claveAnteriorCifrada);

        BytesEncryptor decryptor = Encryptors.stronger(contrasenaActual, saltAnterior);
        byte[] claveAESBytes = decryptor.decrypt(claveBytesCifrados);

        String nuevoSalt = KeyGenerators.string().generateKey();
        BytesEncryptor encryptor = Encryptors.stronger(nuevaContrasena, nuevoSalt);
        byte[] nuevaClaveCifrada = encryptor.encrypt(claveAESBytes);
        String nuevaClaveFinal = nuevoSalt + ":" + Base64.getEncoder().encodeToString(nuevaClaveCifrada);

        usuario.setClaveCifDesPersonal(nuevaClaveFinal);
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));

        RepoUsuario.save(usuario);
    }

    @Override
    public void recuperarContrasenaSinToken(String correo, String contrasenaActual, String nuevaContrasena) {
        Usuario usuario = RepoUsuario.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            throw new RuntimeException("Contraseña actual incorrecta.");
        }

        String cifrado = usuario.getClaveCifDesPersonal();
        if (cifrado == null || !cifrado.contains(":")) {
            throw new RuntimeException("Clave cifrada inválida.");
        }

        String[] partes = cifrado.split(":");
        String saltAnterior = partes[0];
        String claveAnteriorCifrada = partes[1];

        byte[] claveCifradaBytes = Base64.getDecoder().decode(claveAnteriorCifrada);
        BytesEncryptor decryptor = Encryptors.stronger(contrasenaActual, saltAnterior);
        byte[] claveAESBytes = decryptor.decrypt(claveCifradaBytes);

        String nuevoSalt = KeyGenerators.string().generateKey();
        BytesEncryptor encryptor = Encryptors.stronger(nuevaContrasena, nuevoSalt);
        byte[] nuevaClaveCifrada = encryptor.encrypt(claveAESBytes);
        String nuevaClaveFinal = nuevoSalt + ":" + Base64.getEncoder().encodeToString(nuevaClaveCifrada);

        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuario.setClaveCifDesPersonal(nuevaClaveFinal);
        RepoUsuario.save(usuario);
    }
}
