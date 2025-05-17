package org.CMVD.Softwork.Fileshield.Repositorios;

import org.CMVD.Softwork.Fileshield.Model.EnvioCorreo;
import org.CMVD.Softwork.Fileshield.Model.RecepcionCorreo;
import org.CMVD.Softwork.Fileshield.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface RecepcionCorreoRepositorio extends JpaRepository<RecepcionCorreo, Integer> {
    Optional<RecepcionCorreo> findByFechaRecepcion(Date fechaRecepcion);
    Optional<RecepcionCorreo> findByEnvioRecepcion(EnvioCorreo envioRecepcion);
    Optional<RecepcionCorreo> findByUsuarioRecepcion(Usuario usuarioRecepcion);

    @Override
    Optional<RecepcionCorreo> findById(Integer idRecepcionCorreo);
}
