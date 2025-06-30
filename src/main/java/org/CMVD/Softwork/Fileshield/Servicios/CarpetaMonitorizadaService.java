package org.CMVD.Softwork.Fileshield.Servicios;

import org.CMVD.Softwork.Fileshield.DTO.Carpeta.CarpetaMonitorizadaDTO;
import javax.crypto.SecretKey;
import java.util.List;

public interface CarpetaMonitorizadaService {
    CarpetaMonitorizadaDTO registrarCarpeta(CarpetaMonitorizadaDTO carpetaDTO);
    List<CarpetaMonitorizadaDTO> obtenerCarpetasPorUsuario(Integer idUsuario);
    void eliminarCarpeta(Integer idCarpetaMonitorizada);
    void iniciarMonitoreo(String rutaCarpeta, SecretKey clave);
    void detenerMonitoreo(String rutaCarpeta);
}
