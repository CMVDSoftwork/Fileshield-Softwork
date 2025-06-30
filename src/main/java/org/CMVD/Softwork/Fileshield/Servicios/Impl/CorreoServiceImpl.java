package org.CMVD.Softwork.Fileshield.Servicios.Impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.CMVD.Softwork.Fileshield.DTO.Correo.CorreoRequest;
import org.CMVD.Softwork.Fileshield.DTO.Correo.EnvioCorreoDTO;
import org.CMVD.Softwork.Fileshield.DTO.Correo.RecepcionCorreoDTO;
import org.CMVD.Softwork.Fileshield.Model.*;
import org.CMVD.Softwork.Fileshield.Repositorios.*;
import org.CMVD.Softwork.Fileshield.Servicios.CifradorAESService;
import org.CMVD.Softwork.Fileshield.Servicios.CorreoService;
import org.CMVD.Softwork.Fileshield.Servicios.EnlaceSeguroService;
import org.CMVD.Softwork.Fileshield.Servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CorreoServiceImpl implements CorreoService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private CorreoRepositorio RepoCorreo;
    @Autowired
    private EnvioCorreoRepositorio RepoEnvio;
    @Autowired
    private RecepcionCorreoRepositorio RepoRecepcion;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CifradorAESService cifradorAES;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private CorreoRepositorio correoRepositorio;
    @Autowired
    private EnvioCorreoRepositorio envioCorreoRepositorio;
    @Autowired
    private RecepcionCorreoRepositorio recepcionCorreoRepositorio;
    @Autowired
    private ArchivoCorreoRepositorio archivoCorreoRepositorio;
    @Autowired
    private EnlaceSeguroService enlaceSeguroService;

    @Override
    public EnlaceSeguro enviarCorreoConCifrado(CorreoRequest dto, List<MultipartFile> adjuntos) throws Exception {
        String correoUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario emisor = usuarioRepositorio.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Usuario receptor = usuarioRepositorio.findByCorreo(dto.getDestinatario())
                .orElseThrow(() -> new RuntimeException("Receptor no encontrado"));

        SecretKey clave = cifradorAES.generarClave();

        String cuerpoCifrado = cifradorAES.cifrarTexto(dto.getCuerpoPlano(), clave);
        String claveBase64 = Base64.getEncoder().encodeToString(cifradorAES.claveABytes(clave));

        Correo correo = new Correo();
        correo.setContenidoCifrado(cuerpoCifrado);
        correo.setClaveCifDes(claveBase64);
        correo.setEstatus("ENVIADO");
        correoRepositorio.save(correo);

        for (MultipartFile adjunto : adjuntos) {
            File original = File.createTempFile("original_", "_" + adjunto.getOriginalFilename());
            adjunto.transferTo(original);

            ByteArrayOutputStream cifradoOutputStream = new ByteArrayOutputStream();
            cifradorAES.cifrarArchivoStream(original, cifradoOutputStream, clave);
            byte[] contenidoCifrado = cifradoOutputStream.toByteArray();

            ArchivoCorreo archivoCorreo = new ArchivoCorreo();
            archivoCorreo.setNombreOriginal(adjunto.getOriginalFilename());
            archivoCorreo.setContenidoCifrado(contenidoCifrado);
            archivoCorreo.setCorreo(correo);

            archivoCorreoRepositorio.save(archivoCorreo);

            original.delete();
        }

        EnvioCorreo envio = new EnvioCorreo();
        envio.setCorreo(correo);
        envio.setUsuarioEmisor(emisor);
        envio.setFechaEnvio(new Date());
        envioCorreoRepositorio.save(envio);

        RecepcionCorreo recepcion = new RecepcionCorreo();
        recepcion.setEnvioRecepcion(envio);
        recepcion.setUsuarioRecepcion(receptor);
        recepcion.setFechaRecepcion(new Date());
        recepcionCorreoRepositorio.save(recepcion);

        EnlaceSeguro enlace = enlaceSeguroService.generarEnlaceSeguro(correo, receptor, claveBase64);

        enviarCorreoNotificacion(emisor, receptor, correo, enlace);

        return enlace;
    }


    @Override
    public void enviarCorreoNotificacion(Usuario emisor, Usuario receptor, Correo correo, EnlaceSeguro enlace) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

        String enlaceSeguro = "http://localhost:8080/api/enlaces/" + enlace.getTokenUnico() + "/validar";

        String cuerpoHTML = """
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>🔐 Has recibido un archivo seguro</title>
    <style>
        body {
            background-color: #0B0F1A;
            color: #FFFFFF;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            padding: 40px;
        }
        .container {
            max-width: 600px;
            margin: auto;
            background-color: #111520;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 0 12px rgba(30,144,255,0.2);
        }
        .logo {
            display: block;
            margin: auto;
            margin-bottom: 20px;
            width: 140px;
        }
        h2 {
            color: #1E90FF;
            text-align: center;
        }
        p, li {
            color: #CCCCCC;
            line-height: 1.6;
        }
        ol {
            margin-left: 20px;
        }
        .btn {
            display: inline-block;
            margin-top: 20px;
            padding: 12px 20px;
            background-color: #1E90FF;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-weight: bold;
        }
        .footer {
            margin-top: 40px;
            color: #888888;
            font-size: 13px;
            text-align: center;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>🔐 Has recibido un mensaje seguro</h2>
        <p>Hola,</p>
        <p>Has recibido un mensaje o archivo cifrado a través de <strong>FileShield</strong>. Para acceder al contenido, sigue estos pasos:</p>
        <ol>
            <li>Abre la aplicación <strong>FileShield</strong> en tu equipo.</li>
            <li>Inicia sesión con tu correo registrado.</li>
            <li>Ve a la sección <strong>“Ver clave”</strong>.</li>
            <li>Pega el siguiente enlace cuando se te solicite:</li>
        </ol>
        <a href="%s" class="btn">Abrir Enlace Seguro</a>
        <p class="footer">
            Este enlace expira en 24 horas y solo puede usarse una vez.<br>
            Si no esperabas este correo, ignóralo.
        </p>
    </div>
</body>
</html>
""".formatted(enlaceSeguro);
        helper.setTo(receptor.getCorreo());
        helper.setSubject("🔐 Nuevo mensaje cifrado recibido");
        helper.setText(cuerpoHTML, true);
        helper.setFrom(emisor.getCorreo());

        List<ArchivoCorreo> archivos = archivoCorreoRepositorio.findByCorreo(correo);
        for (ArchivoCorreo archivo : archivos) {
            helper.addAttachment(archivo.getNombreOriginal() + ".enc", new ByteArrayResource(archivo.getContenidoCifrado()));
        }

        mailSender.send(mensaje);
    }


    @Override
    public List<EnvioCorreoDTO> listarCorreosEnviados(String correoUsuario) {
        Usuario usuario = usuarioRepositorio.findByCorreo(correoUsuario).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<EnvioCorreo> envios = envioCorreoRepositorio.findByUsuarioEmisorOrderByFechaEnvioDesc(usuario);

        return envios.stream()
                .map(EnvioCorreoDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecepcionCorreoDTO> listarCorreosRecibidos(String correoUsuario) {
        Usuario usuario = usuarioRepositorio.findByCorreo(correoUsuario).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<RecepcionCorreo> recepciones = recepcionCorreoRepositorio.findByUsuarioRecepcionOrderByFechaRecepcionDesc(usuario);

        return recepciones.stream()
                .map(RecepcionCorreoDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] descifrarAdjuntoCorreo(Integer idArchivoCorreo, String claveBase64) throws Exception {
        ArchivoCorreo archivoAdjunto = archivoCorreoRepositorio.findById(idArchivoCorreo)
                .orElseThrow(() -> new RuntimeException("Adjunto de correo no encontrado con ID: " + idArchivoCorreo));

        SecretKey clave = cifradorAES.bytesAClave(Base64.getDecoder().decode(claveBase64));
        return cifradorAES.descifrarBytes(archivoAdjunto.getContenidoCifrado(), clave);
    }
}
