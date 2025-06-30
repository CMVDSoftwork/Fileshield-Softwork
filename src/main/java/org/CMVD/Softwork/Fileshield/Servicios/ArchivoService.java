package org.CMVD.Softwork.Fileshield.Servicios;

import org.CMVD.Softwork.Fileshield.DTO.Carpeta.ArchivoDTO;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface ArchivoService {
    List<ArchivoDTO> obtenerArchivosPorCarpeta(Integer idCarpetaMonitorizada);
    List<ArchivoDTO> obtenerTodosLosArchivos();
    byte[] descifrarArchivo(Integer idArchivo, String claveBase64) throws IOException, GeneralSecurityException;
    void eliminarArchivo(Integer idArchivo);
}
