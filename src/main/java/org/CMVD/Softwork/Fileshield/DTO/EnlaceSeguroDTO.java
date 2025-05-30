package org.CMVD.Softwork.Fileshield.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.CMVD.Softwork.Fileshield.Model.EnlaceSeguro;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EnlaceSeguroDTO {
    private String tokenUnico;
    private LocalDateTime fechaExpiracion;
    private String correoReceptor;
    private Integer idCorreo;

    public EnlaceSeguroDTO(EnlaceSeguro p_enlaceSeguro) {
        this.tokenUnico = p_enlaceSeguro.getTokenUnico();
        this.fechaExpiracion = p_enlaceSeguro.getFechaExpiracion();
        this.idCorreo = p_enlaceSeguro.getCorreo().getIdCorreo();
    }
}
