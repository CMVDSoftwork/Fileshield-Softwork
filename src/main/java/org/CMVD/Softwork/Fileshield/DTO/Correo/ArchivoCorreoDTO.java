package org.CMVD.Softwork.Fileshield.DTO.Correo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.CMVD.Softwork.Fileshield.Model.ArchivoCorreo;

@Data
@NoArgsConstructor
public class ArchivoCorreoDTO {
    private Integer idArchivoCorreo;
    private String nombreOriginal;

    public ArchivoCorreoDTO(ArchivoCorreo archivoCorreo) {
        this.idArchivoCorreo = archivoCorreo.getIdArchivoCorreo();
        this.nombreOriginal = archivoCorreo.getNombreOriginal();
    }
}
