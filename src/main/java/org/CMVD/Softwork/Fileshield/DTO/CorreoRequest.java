package org.CMVD.Softwork.Fileshield.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CorreoRequest {
    private String asunto;
    private String cuerpoPlano; // se va a cifrar
    private String destinatario; // o lista si es múltiple
    private String emisor;
}
