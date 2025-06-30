package org.CMVD.Softwork.Fileshield.DTO.Carpeta;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.CMVD.Softwork.Fileshield.DTO.SessionRequest.UsuarioDTO;
import org.CMVD.Softwork.Fileshield.Model.Archivo;
import java.util.Date;

@Data
@NoArgsConstructor
public class ArchivoDTO {
    private Integer idArchivo;
    private String nombreArchivo, estado,tipoArchivo;
    private String rutaArchivo;
    private int tamaño;
    private Date fechaSubida;

    private UsuarioDTO usuarioDTO;
    private CarpetaMonitorizadaDTO carpetaMonitorizadaDTO;

    public ArchivoDTO(Archivo p_archivo) {
        this.idArchivo = p_archivo.getIdArchivo();
        this.nombreArchivo = p_archivo.getNombreArchivo();
        this.estado = p_archivo.getEstado();
        this.tipoArchivo = p_archivo.getTipoArchivo();
        this.rutaArchivo = p_archivo.getRutaArchivo();
        this.tamaño = p_archivo.getTamaño();
        this.fechaSubida = p_archivo.getFechaSubida();
        usuarioDTO = new UsuarioDTO(p_archivo.getUsuario());
        carpetaMonitorizadaDTO = new CarpetaMonitorizadaDTO(p_archivo.getCarpetaMonitorizada());
    }
}
