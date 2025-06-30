package org.CMVD.Softwork.Fileshield.DTO.Carpeta;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.CMVD.Softwork.Fileshield.DTO.SessionRequest.UsuarioDTO;
import org.CMVD.Softwork.Fileshield.Model.CarpetaMonitorizada;

@Data
@NoArgsConstructor
public class CarpetaMonitorizadaDTO {
    private Integer idCarpetaMonitorizada;
    private String ruta;
    private UsuarioDTO usuarioDTO;

    private String estado; // NUEVO CAMPO

    public CarpetaMonitorizadaDTO(CarpetaMonitorizada p_carpetaMonitorizada) {
        this.idCarpetaMonitorizada = p_carpetaMonitorizada.getIdCarpetaMonitorizada();
        this.ruta = p_carpetaMonitorizada.getRuta();
        this.usuarioDTO = new UsuarioDTO(p_carpetaMonitorizada.getUsuario());
        this.estado = "EN MONITOREO";
    }
}
