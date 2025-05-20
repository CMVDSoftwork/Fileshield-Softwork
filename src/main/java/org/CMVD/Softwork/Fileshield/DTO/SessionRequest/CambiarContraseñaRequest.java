package org.CMVD.Softwork.Fileshield.SessionRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CambiarContraseñaRequest {
    @NotBlank(message = "Es necesario ingresar su contraseña")
    private String contrasenaActual;
    @NotBlank
    private String nuevaContrasena;
}
