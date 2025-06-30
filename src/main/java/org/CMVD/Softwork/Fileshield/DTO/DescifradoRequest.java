package org.CMVD.Softwork.Fileshield.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DescifradoRequest {
    private String textoCifradoBase64;
    private String claveBase64;
}
