package org.CMVD.Softwork.Fileshield.SessionRequest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
    private String correo;
    private String contrasena;

}
