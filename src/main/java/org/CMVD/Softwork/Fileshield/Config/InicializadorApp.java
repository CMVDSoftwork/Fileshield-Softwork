package org.CMVD.Softwork.Fileshield.Config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.io.File;

@Component
public class InicializadorApp {
    private final RutaArchivosProperties rutaArchivos;

    public InicializadorApp(RutaArchivosProperties rutaArchivos) {
        this.rutaArchivos = rutaArchivos;
    }

    @PostConstruct
    public void crearCarpetaCifrados() {
        File carpeta = new File(rutaArchivos.getCifrados());
        if (!carpeta.exists()) {
            carpeta.mkdirs();
            System.out.println("Carpeta de archivos cifrados creada en: " + carpeta.getAbsolutePath());
        }
    }
}
