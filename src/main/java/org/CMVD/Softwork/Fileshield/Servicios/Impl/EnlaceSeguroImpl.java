package org.CMVD.Softwork.Fileshield.Servicios.Impl;

import org.CMVD.Softwork.Fileshield.DTO.Correo.ArchivoCorreoDTO;
import org.CMVD.Softwork.Fileshield.DTO.Correo.CorreoDTO;
import org.CMVD.Softwork.Fileshield.Model.*;
import org.CMVD.Softwork.Fileshield.Repositorios.ArchivoCorreoRepositorio;
import org.CMVD.Softwork.Fileshield.Repositorios.CorreoRepositorio;
import org.CMVD.Softwork.Fileshield.Repositorios.EnlaceSeguroRepositorio;
import org.CMVD.Softwork.Fileshield.Repositorios.RecepcionCorreoRepositorio;
import org.CMVD.Softwork.Fileshield.Servicios.EnlaceSeguroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EnlaceSeguroImpl implements EnlaceSeguroService {
    @Autowired
    private CorreoRepositorio correoRepositorio;

    @Autowired
    private ArchivoCorreoRepositorio archivoCorreoRepositorio;

    @Autowired
    private EnlaceSeguroRepositorio enlaceSeguroRepositorio;

    @Autowired
    private RecepcionCorreoRepositorio recepcionCorreoRepositorio;

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
    public Optional<CorreoDTO> validarToken(String token, String correoUsuario) {
        return enlaceSeguroRepositorio.findByTokenUnico(token)
                .filter(e -> !e.isUsado())
                .filter(e -> e.getFechaExpiracion().isAfter(LocalDateTime.now()))
                .filter(e -> e.getReceptor().getCorreo().equalsIgnoreCase(correoUsuario))
                .map(enlaceSeguro -> {
                    enlaceSeguro.setUsado(true);
                    enlaceSeguroRepositorio.save(enlaceSeguro);

                    RecepcionCorreo recepcion = new RecepcionCorreo();
                    recepcion.setFechaRecepcion(java.util.Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                    recepcion.setUsuarioRecepcion(enlaceSeguro.getReceptor());
                    recepcion.setEnvioRecepcion(enlaceSeguro.getCorreo().getEnvioCorreo());
                    recepcionCorreoRepositorio.save(recepcion);

                    Correo correo = enlaceSeguro.getCorreo();
                    CorreoDTO correoDTO = new CorreoDTO(correo);

                    List<ArchivoCorreo> archivosEntidad = archivoCorreoRepositorio.findByCorreo_IdCorreo(correo.getIdCorreo());
                    List<ArchivoCorreoDTO> archivosDTO = archivosEntidad.stream()
                            .map(ArchivoCorreoDTO::new)
                            .collect(Collectors.toList());
                    correoDTO.setArchivosAdjuntos(archivosDTO);

                    return correoDTO;
                });
    }
}

