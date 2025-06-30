package org.CMVD.Softwork.Fileshield.DTO.SessionRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecuperarContraseñaRequest {
    @NotBlank
    private String correo;
    @NotBlank(message = "Es necesario ingresar su contraseña")
    private String contrasenaActual;
    @NotBlank
    private String nuevaContrasena;
}
