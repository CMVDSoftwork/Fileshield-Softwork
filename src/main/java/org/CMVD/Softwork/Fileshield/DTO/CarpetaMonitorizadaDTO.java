package org.CMVD.Softwork.Fileshield.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.CMVD.Softwork.Fileshield.Model.CarpetaMonitorizada;

@Data
@NoArgsConstructor
public class CarpetaMonitorizadaDTO {
    private Integer idCarpetaMonitorizada;
    private String ruta;
    private UsuarioDTO usuarioDTO;

    public CarpetaMonitorizadaDTO(CarpetaMonitorizada p_carpetaMonitorizada) {
        this.idCarpetaMonitorizada = p_carpetaMonitorizada.getIdCarpetaMonitorizada();
        this.ruta = p_carpetaMonitorizada.getRuta();
        usuarioDTO = new UsuarioDTO(p_carpetaMonitorizada.getUsuario()) ;
    }
}
