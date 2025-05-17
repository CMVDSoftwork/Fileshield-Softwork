package org.CMVD.Softwork.Fileshield.Servicios.Impl;

import org.CMVD.Softwork.Fileshield.DTO.UsuarioDTO;
import org.CMVD.Softwork.Fileshield.Model.Usuario;
import org.CMVD.Softwork.Fileshield.Repositorios.UsuarioRepositorio;
import org.CMVD.Softwork.Fileshield.Security.JwtTokenProvider;
import org.CMVD.Softwork.Fileshield.Servicios.UsuarioService;
import org.CMVD.Softwork.Fileshield.SessionRequest.LoginRequest;
import org.CMVD.Softwork.Fileshield.SessionRequest.LoginResponse;
import org.CMVD.Softwork.Fileshield.SessionRequest.RegistroRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepositorio RepoUsuario;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public UsuarioDTO registrar(RegistroRequest request) {
        if (RepoUsuario.findByCorreo(request.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado.");
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(request.getNombre());
        nuevo.setCorreo(request.getCorreo());
        nuevo.setContrasena(passwordEncoder.encode(request.getContrasena()));

        Usuario guardado = RepoUsuario.save(nuevo);

        return new UsuarioDTO(guardado);
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = RepoUsuario.findByCorreo(request.getCorreo()).orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Contraseña incorrecta.");
        }

        String token = jwtTokenProvider.generarToken(usuario);
        return new LoginResponse(token, usuario.getNombre(), usuario.getCorreo());
    }

}
