package org.CMVD.Softwork.Fileshield.Repositorios;

import org.CMVD.Softwork.Fileshield.Model.Correo;
import org.CMVD.Softwork.Fileshield.Model.EnvioCorreo;
import org.CMVD.Softwork.Fileshield.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface EnvioCorreoRepositorio extends JpaRepository<EnvioCorreo, Integer> {
    Optional<EnvioCorreo> findByFechaEnvio(Date fechaEnvio);
    Optional<EnvioCorreo> findByCorreo(Correo correo);
    Optional<EnvioCorreo> findByUsuarioEmisor(Usuario usuarioEmisor);

    @Override
    Optional<EnvioCorreo> findById(Integer idEnvioCorreo);
}
