package org.CMVD.Softwork.Fileshield.Servicios;

import jakarta.mail.MessagingException;
import org.CMVD.Softwork.Fileshield.DTO.Correo.CorreoRequest;
import org.CMVD.Softwork.Fileshield.DTO.Correo.EnvioCorreoDTO;
import org.CMVD.Softwork.Fileshield.DTO.Correo.RecepcionCorreoDTO;
import org.CMVD.Softwork.Fileshield.Model.Correo;
import org.CMVD.Softwork.Fileshield.Model.EnlaceSeguro;
import org.CMVD.Softwork.Fileshield.Model.Usuario;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface CorreoService {
    EnlaceSeguro enviarCorreoConCifrado(CorreoRequest dto, List<MultipartFile> adjuntos) throws Exception;
    void enviarCorreoNotificacion(Usuario emisor, Usuario receptor, Correo correo, EnlaceSeguro enlace) throws MessagingException;
    List<EnvioCorreoDTO> listarCorreosEnviados(String correoUsuario);
    List<RecepcionCorreoDTO> listarCorreosRecibidos(String correoUsuario);
    byte[] descifrarAdjuntoCorreo(Integer idArchivoCorreo, String claveBase64) throws Exception;
}
