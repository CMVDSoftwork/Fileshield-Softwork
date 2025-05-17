package org.CMVD.Softwork.Fileshield.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.CMVD.Softwork.Fileshield.Model.EnlaceSeguro;

@Data
@NoArgsConstructor
public class EnlaceSeguroDTO {
    private Integer idEnlaceSeguro;
    private String url, estado;

    public EnlaceSeguroDTO(EnlaceSeguro p_enlaceSeguro) {
        this.idEnlaceSeguro = p_enlaceSeguro.getIdEnlaceSeguro();
        this.url = p_enlaceSeguro.getUrl();
        this.estado = p_enlaceSeguro.getEstado();
    }
}
