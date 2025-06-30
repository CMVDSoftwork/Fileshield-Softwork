package org.CMVD.Softwork.Fileshield.Repositorios;

import org.CMVD.Softwork.Fileshield.Model.ArchivoCorreo;
import org.CMVD.Softwork.Fileshield.Model.Correo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArchivoCorreoRepositorio extends JpaRepository<ArchivoCorreo, Integer> {
    List<ArchivoCorreo> findByCorreo(Correo correo);
    List<ArchivoCorreo> findByCorreo_IdCorreo(Integer idCorreo);
}
