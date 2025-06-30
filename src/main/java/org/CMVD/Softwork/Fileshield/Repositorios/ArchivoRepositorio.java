package org.CMVD.Softwork.Fileshield.Repositorios;

import org.CMVD.Softwork.Fileshield.Model.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArchivoRepositorio extends JpaRepository<Archivo, Integer> {
    Optional<Archivo> findByNombreArchivo(String nombreArchivo);
    Optional<Archivo> findByEstado(String estado);
    Optional<Archivo> findByNombreArchivoAndEstado(String nombreArchivo, String estado);
    Optional<Archivo> findByTamaño(int tamaño);
    Optional<Archivo> findByTipoArchivo(String tipoArchivo);
    Optional<Archivo> findArchivoByFechaSubida(Date fechaSubida);
    List<Archivo> findByCarpetaMonitorizada_IdCarpetaMonitorizada(Integer idCarpetaMonitorizada);
    List<Archivo> findByUsuario_IdUsuario(Integer idUsuario);

    @Override
    Optional<Archivo> findById(Integer idArchivo);
}
