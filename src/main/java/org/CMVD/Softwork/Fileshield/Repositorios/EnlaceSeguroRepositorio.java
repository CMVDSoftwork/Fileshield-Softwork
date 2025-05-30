package org.CMVD.Softwork.Fileshield.Repositorios;

import org.CMVD.Softwork.Fileshield.Model.EnlaceSeguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EnlaceSeguroRepositorio extends JpaRepository<EnlaceSeguro, Integer> {
    Optional<EnlaceSeguro> findByTokenUnico(String token);

    @Override
    Optional<EnlaceSeguro> findById(Integer integer);
}
