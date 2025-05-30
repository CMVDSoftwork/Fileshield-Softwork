package org.CMVD.Softwork.Fileshield.DTO.SessionRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistroRequest {
    @NotBlank
    private String nombre;
    private String apellidoP;
    private String apellidoM;

    @Email
    @NotBlank
    private String correo;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;
}
