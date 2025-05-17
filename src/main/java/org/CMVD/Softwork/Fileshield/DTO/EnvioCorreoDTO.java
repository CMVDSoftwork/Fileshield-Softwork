package org.CMVD.Softwork.Fileshield.DTO;

import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.CMVD.Softwork.Fileshield.Model.Correo;
import org.CMVD.Softwork.Fileshield.Model.EnvioCorreo;
import org.CMVD.Softwork.Fileshield.Model.Usuario;

import java.util.Date;

@Data
@NoArgsConstructor
public class EnvioCorreoDTO {
    private Integer idEnvioCorreo;
    private Date fechaEnvio;
    private UsuarioDTO usuarioEmisorDTO;
    private CorreoDTO correoDTO;

    public EnvioCorreoDTO(EnvioCorreo p_envioCorreo) {
        this.idEnvioCorreo = p_envioCorreo.getIdEnvioCorreo();
        this.fechaEnvio = p_envioCorreo.getFechaEnvio();
        usuarioEmisorDTO = new UsuarioDTO(p_envioCorreo.getUsuarioEmisor());
        correoDTO = new CorreoDTO(p_envioCorreo.getCorreo());
    }
}
