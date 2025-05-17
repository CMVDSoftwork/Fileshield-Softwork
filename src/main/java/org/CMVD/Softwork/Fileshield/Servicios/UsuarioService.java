package org.CMVD.Softwork.Fileshield.Servicios;

import org.CMVD.Softwork.Fileshield.DTO.UsuarioDTO;
import org.CMVD.Softwork.Fileshield.SessionRequest.LoginRequest;
import org.CMVD.Softwork.Fileshield.SessionRequest.LoginResponse;
import org.CMVD.Softwork.Fileshield.SessionRequest.RegistroRequest;

public interface UsuarioService {
    UsuarioDTO registrar(RegistroRequest request);
    LoginResponse login(LoginRequest request);

}
