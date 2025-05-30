package org.CMVD.Softwork.Fileshield.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.CMVD.Softwork.Fileshield.Model.RecepcionCorreo;
import java.util.Date;

@Data
@NoArgsConstructor
public class RecepcionCorreoDTO {
    private Integer idRecepcionCorreo;
    private Date fechaRecepcion;
    private UsuarioDTO usuarioRecepcionDTO;
    private EnvioCorreoDTO envioRecepcionDTO;

    public RecepcionCorreoDTO(RecepcionCorreo p_recepcionCorreo) {
        this.idRecepcionCorreo = p_recepcionCorreo.getIdRecepcionCorreo();
        this.fechaRecepcion = p_recepcionCorreo.getFechaRecepcion();
        usuarioRecepcionDTO = new UsuarioDTO(p_recepcionCorreo.getUsuarioRecepcion());
        envioRecepcionDTO = new EnvioCorreoDTO(p_recepcionCorreo.getEnvioRecepcion());
    }
}
