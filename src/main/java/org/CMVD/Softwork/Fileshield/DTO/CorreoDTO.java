package org.CMVD.Softwork.Fileshield.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.CMVD.Softwork.Fileshield.Model.Correo;

@Data
@NoArgsConstructor
public class CorreoDTO {
    private Integer idCorreo;
    private String contenidoCifrado, claveCifDes, estatus;

    public CorreoDTO(Correo p_correo) {
        this.idCorreo = p_correo.getIdCorreo();
        this.contenidoCifrado = p_correo.getContenidoCifrado();
        this.claveCifDes = p_correo.getClaveCifDes();
        this.estatus = p_correo.getEstatus();
    }
}
