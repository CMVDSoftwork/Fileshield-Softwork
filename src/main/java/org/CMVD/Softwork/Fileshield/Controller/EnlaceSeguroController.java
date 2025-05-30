package org.CMVD.Softwork.Fileshield.Controller;

import org.CMVD.Softwork.Fileshield.DTO.CorreoDTO;
import org.CMVD.Softwork.Fileshield.DTO.EnlaceSeguroDTO;
import org.CMVD.Softwork.Fileshield.Model.Correo;
import org.CMVD.Softwork.Fileshield.Model.EnlaceSeguro;
import org.CMVD.Softwork.Fileshield.Model.RecepcionCorreo;
import org.CMVD.Softwork.Fileshield.Repositorios.CorreoRepositorio;
import org.CMVD.Softwork.Fileshield.Repositorios.EnlaceSeguroRepositorio;
import org.CMVD.Softwork.Fileshield.Repositorios.RecepcionCorreoRepositorio;
import org.CMVD.Softwork.Fileshield.Servicios.EnlaceSeguroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/enlaces")
public class EnlaceSeguroController {
    @Autowired
    private EnlaceSeguroService enlaceSeguroService;

    @Autowired
    private EnlaceSeguroRepositorio RepoEnlaceSeguro;

    @Autowired
    private CorreoRepositorio RepoCorreo;
    @Autowired
    private RecepcionCorreoRepositorio recepcionCorreoRepositorio;


    @GetMapping("/{token}/validar")
    public ResponseEntity<?> validarTokenAntesDeLogin(@PathVariable String token) {
        Optional<EnlaceSeguro> optional = RepoEnlaceSeguro.findByTokenUnico(token);

        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Enlace no encontrado.");
        }

        EnlaceSeguro enlace = optional.get();

        if (enlace.isUsado()) {
            return ResponseEntity.status(HttpStatus.GONE).body("Este enlace ya ha sido utilizado.");
        }

        if (enlace.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.GONE).body("Este enlace ha expirado.");
        }

        return ResponseEntity.ok(new EnlaceSeguroDTO(enlace));
    }


    @GetMapping("/{token}")
    public ResponseEntity<?> accederClave(@PathVariable String token, @RequestParam String correoUsuario) {
        Optional<EnlaceSeguro> optional = enlaceSeguroService.validarToken(token, correoUsuario);

        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token inválido o expirado");
        }

        EnlaceSeguro enlace = optional.get();

        if (!enlace.getReceptor().getCorreo().equalsIgnoreCase(correoUsuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Este correo no está autorizado.");
        }

        enlace.setUsado(true);
        RepoEnlaceSeguro.save(enlace);

        RecepcionCorreo recepcion = new RecepcionCorreo();
        recepcion.setFechaRecepcion(new Date());
        recepcion.setUsuarioRecepcion(enlace.getReceptor());
        recepcion.setEnvioRecepcion(enlace.getCorreo().getEnvioCorreo());
        recepcionCorreoRepositorio.save(recepcion);

        Correo correo = enlace.getCorreo();

        return ResponseEntity.ok(new CorreoDTO(correo));
    }
}
