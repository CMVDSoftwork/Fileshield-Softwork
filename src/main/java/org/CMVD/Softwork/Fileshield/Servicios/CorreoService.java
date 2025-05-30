package org.CMVD.Softwork.Fileshield.Servicios;

import jakarta.mail.MessagingException;
import org.CMVD.Softwork.Fileshield.DTO.CorreoRequest;
import org.CMVD.Softwork.Fileshield.DTO.EnvioCorreoDTO;
import org.CMVD.Softwork.Fileshield.DTO.RecepcionCorreoDTO;
import org.CMVD.Softwork.Fileshield.Model.Correo;
import org.CMVD.Softwork.Fileshield.Model.Usuario;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface CorreoService {
    void enviarCorreoConCifrado(CorreoRequest dto, List<MultipartFile> adjuntos)throws Exception;
    void enviarCorreoNotificacion(Usuario emisor, Usuario receptor, Correo correo)throws MessagingException;
    List<EnvioCorreoDTO> listarCorreosEnviados(String correoUsuario);
    List<RecepcionCorreoDTO> listarCorreosRecibidos(String correoUsuario);
}
