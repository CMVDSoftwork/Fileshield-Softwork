package org.CMVD.Softwork.Fileshield.SessionRequest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistroRequest {
    private String nombre;
    private String apellidoP;
    private String apellidoM;
    private String correo;
    private String contrasena;
}
