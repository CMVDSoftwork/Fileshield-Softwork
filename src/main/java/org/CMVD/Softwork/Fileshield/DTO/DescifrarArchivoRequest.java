package org.CMVD.Softwork.Fileshield.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DescifrarArchivoRequest {
    private Integer idArchivo;
    private String clavePersonal;
}
