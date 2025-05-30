package org.CMVD.Softwork.Fileshield.Servicios.Impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.CMVD.Softwork.Fileshield.Config.RutaArchivosProperties;
import org.CMVD.Softwork.Fileshield.DTO.CorreoRequest;
import org.CMVD.Softwork.Fileshield.DTO.EnvioCorreoDTO;
import org.CMVD.Softwork.Fileshield.DTO.RecepcionCorreoDTO;
import org.CMVD.Softwork.Fileshield.Model.*;
import org.CMVD.Softwork.Fileshield.Repositorios.*;
import org.CMVD.Softwork.Fileshield.Servicios.CifradorAESService;
import org.CMVD.Softwork.Fileshield.Servicios.CorreoService;
import org.CMVD.Softwork.Fileshield.Servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.crypto.SecretKey;
import java.io.File;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
    private RutaArchivosProperties rutaArchivos;

    @Override
    public void enviarCorreoConCifrado(CorreoRequest dto, List<MultipartFile> adjuntos) throws Exception {
        Usuario emisor = usuarioRepositorio.findByCorreo(dto.getEmisor()).orElseThrow(() -> new RuntimeException("Emisor no encontrado"));
        Usuario receptor = usuarioRepositorio.findByCorreo(dto.getDestinatario()).orElseThrow(() -> new RuntimeException("Receptor no encontrado"));

        SecretKey clave = cifradorAES.generarClave();
        String cuerpoCifrado = cifradorAES.cifrarTexto(dto.getCuerpoPlano(), clave);
        String claveBase64 = Base64.getEncoder().encodeToString(cifradorAES.claveABytes(clave));

        Correo correo = new Correo();
        correo.setContenidoCifrado(cuerpoCifrado);
        correo.setClaveCifDes(claveBase64);
        correo.setEstatus("ENVIADO");
        correoRepositorio.save(correo);

        File carpeta = new File(rutaArchivos.getCifrados());
        carpeta.mkdirs();

        for (MultipartFile adjunto : adjuntos) {
            File original = File.createTempFile("original_", "_" + adjunto.getOriginalFilename());
            adjunto.transferTo(original);

            String nombreArchivo = UUID.randomUUID() + "_" + adjunto.getOriginalFilename();
            File destinoCifrado = new File(rutaArchivos.getCifrados() + "/" + nombreArchivo);

            cifradorAES.cifrarArchivo(original, destinoCifrado, clave);

            ArchivoCorreo archivoCorreo = new ArchivoCorreo();
            archivoCorreo.setNombreOriginal(adjunto.getOriginalFilename());
            archivoCorreo.setRutaCifrado(destinoCifrado.getAbsolutePath());
            archivoCorreo.setCorreo(correo);
            archivoCorreoRepositorio.save(archivoCorreo);
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

        enviarCorreoNotificacion(emisor, receptor, correo);
    }

    @Override
    public void enviarCorreoNotificacion(Usuario emisor, Usuario receptor, Correo correo) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true); // true para adjuntos

        helper.setTo(receptor.getCorreo());
        helper.setSubject("Nuevo mensaje cifrado recibido");
        helper.setText("Has recibido un mensaje cifrado. Ingresa a la app para verlo.", false);
        helper.setFrom(emisor.getCorreo());

        List<ArchivoCorreo> archivos = archivoCorreoRepositorio.findByCorreo(correo);
        for (ArchivoCorreo archivo : archivos) {
            FileSystemResource archivoRes = new FileSystemResource(new File(archivo.getRutaCifrado()));
            helper.addAttachment(archivo.getNombreOriginal(), archivoRes);
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
}
