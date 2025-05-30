package org.CMVD.Softwork.Fileshield.Repositorios;

import org.CMVD.Softwork.Fileshield.Model.CarpetaMonitorizada;
import org.CMVD.Softwork.Fileshield.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarpetaMonitorizadaRepositorio extends JpaRepository<CarpetaMonitorizada,Integer> {
    Optional<CarpetaMonitorizada> findCarpetaMonitorizadaByRuta(String ruta);
    Optional<CarpetaMonitorizada> findCarpetaMonitorizadaByUsuario(Usuario usuario);
    List<CarpetaMonitorizada> findByUsuarioIdUsuario(Integer idUsuario);


    @Override
    Optional<CarpetaMonitorizada> findById(Integer idCarpetaMonitorizada);
}
