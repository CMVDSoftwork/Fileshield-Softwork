package org.CMVD.Softwork.Fileshield.Controller;
import org.CMVD.Softwork.Fileshield.DTO.CorreoRequest;
import org.CMVD.Softwork.Fileshield.DTO.EnvioCorreoDTO;
import org.CMVD.Softwork.Fileshield.DTO.RecepcionCorreoDTO;
import org.CMVD.Softwork.Fileshield.Servicios.CorreoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/correos")
@CrossOrigin(origins = "*")
public class CorreoController {
    @Autowired
    private CorreoService correoService;

    @PostMapping("/enviar")
    public ResponseEntity<String> enviarCorreo(
            @RequestPart("correo") CorreoRequest correoRequest,
            @RequestPart(value = "adjuntos", required = false) List<MultipartFile> adjuntos) {
        try {
            correoService.enviarCorreoConCifrado(correoRequest, adjuntos != null ? adjuntos : Collections.emptyList());
            return ResponseEntity.ok("Correo enviado con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar correo: " + e.getMessage());
        }
    }

    @GetMapping("/enviados/{correoUsuario}")
    public ResponseEntity<List<EnvioCorreoDTO>> listarCorreosEnviados(@PathVariable String correoUsuario) {
        try {
            List<EnvioCorreoDTO> enviados = correoService.listarCorreosEnviados(correoUsuario);
            return ResponseEntity.ok(enviados);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/recibidos/{correoUsuario}")
    public ResponseEntity<List<RecepcionCorreoDTO>> listarCorreosRecibidos(@PathVariable String correoUsuario) {
        try {
            List<RecepcionCorreoDTO> recibidos = correoService.listarCorreosRecibidos(correoUsuario);
            return ResponseEntity.ok(recibidos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
