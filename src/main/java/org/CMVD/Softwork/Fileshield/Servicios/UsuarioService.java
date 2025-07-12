package org.CMVD.Softwork.Fileshield.Servicios;

import org.CMVD.Softwork.Fileshield.DTO.SessionRequest.UsuarioDTO;
import org.CMVD.Softwork.Fileshield.DTO.SessionRequest.LoginRequest;
import org.CMVD.Softwork.Fileshield.DTO.SessionRequest.LoginResponse;
import org.CMVD.Softwork.Fileshield.DTO.SessionRequest.RegistroRequest;
import org.CMVD.Softwork.Fileshield.Model.Usuario;
import javax.crypto.SecretKey;
import java.util.Optional;

public interface UsuarioService {
    Optional<Usuario> obtenerPorCorreo(String correo);
    LoginResponse registrar(RegistroRequest request);
    LoginResponse login(LoginRequest request);
    SecretKey recuperarClaveAES(Integer idUsuario, String contrasena);
    void cambiarContrasena(String correo, String contrasenaActual, String nuevaContrasena);
    void recuperarContrasenaSinToken(String correo, String contrasenaActual, String nuevaContrasena);
}
