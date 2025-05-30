package org.CMVD.Softwork.Fileshield.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ruta.archivos")
public class RutaArchivosProperties {
    private String cifrados;

}
