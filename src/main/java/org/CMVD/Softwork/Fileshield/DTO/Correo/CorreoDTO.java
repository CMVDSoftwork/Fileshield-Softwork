package org.CMVD.Softwork.Fileshield.DTO.Correo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.CMVD.Softwork.Fileshield.Model.Correo;

import java.util.List;

@Data
@NoArgsConstructor
public class CorreoDTO {
    private Integer idCorreo;
    private String contenidoCifrado, claveCifDes, estatus;
    private List<ArchivoCorreoDTO> archivosAdjuntos;

    public CorreoDTO(Correo p_correo) {
        this.idCorreo = p_correo.getIdCorreo();
        this.contenidoCifrado = p_correo.getContenidoCifrado();
        this.claveCifDes = p_correo.getClaveCifDes();
        this.estatus = p_correo.getEstatus();
        this.archivosAdjuntos = null;
    }
}
