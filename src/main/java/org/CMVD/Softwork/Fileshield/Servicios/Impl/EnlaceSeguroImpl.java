package org.CMVD.Softwork.Fileshield.Servicios.Impl;

import org.CMVD.Softwork.Fileshield.Model.Correo;
import org.CMVD.Softwork.Fileshield.Model.EnlaceSeguro;
import org.CMVD.Softwork.Fileshield.Model.Usuario;
import org.CMVD.Softwork.Fileshield.Repositorios.EnlaceSeguroRepositorio;
import org.CMVD.Softwork.Fileshield.Servicios.EnlaceSeguroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EnlaceSeguroImpl implements EnlaceSeguroService {
    @Autowired
    private EnlaceSeguroRepositorio enlaceSeguroRepositorio;

    @Override
    public EnlaceSeguro generarEnlaceSeguro(Correo correo, Usuario receptor, String claveBase64) {
        EnlaceSeguro enlace = new EnlaceSeguro();
        enlace.setTokenUnico(UUID.randomUUID().toString());
        enlace.setCorreo(correo);
        enlace.setReceptor(receptor);
        enlace.setClaveCifradoBase64(claveBase64);
        enlace.setFechaExpiracion(LocalDateTime.now().plusHours(24));
        return enlaceSeguroRepositorio.save(enlace);
    }

    @Override
    public Optional<EnlaceSeguro> validarToken(String token, String correoUsuario) {
        return enlaceSeguroRepositorio.findByTokenUnico(token)
                .filter(e -> !e.isUsado())
                .filter(e -> e.getFechaExpiracion().isAfter(LocalDateTime.now()))
                .filter(e -> e.getReceptor().getCorreo().equalsIgnoreCase(correoUsuario));
    }
}

