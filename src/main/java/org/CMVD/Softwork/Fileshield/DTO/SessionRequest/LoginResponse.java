package org.CMVD.Softwork.Fileshield.DTO.SessionRequest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String tipoToken = "Bearer";
    private String correo;
    private String nombre;

    public LoginResponse(String token, String correo, String nombre) {
        this.token = token;
        this.correo = correo;
        this.nombre = nombre;
    }
}
