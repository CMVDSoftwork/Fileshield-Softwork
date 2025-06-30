package org.CMVD.Softwork.Fileshield.Controller;

import org.CMVD.Softwork.Fileshield.Repositorios.ArchivoCorreoRepositorio;
import org.CMVD.Softwork.Fileshield.DTO.Correo.CorreoRequest;
import org.CMVD.Softwork.Fileshield.DTO.Correo.EnvioCorreoDTO;
import org.CMVD.Softwork.Fileshield.DTO.Correo.RecepcionCorreoDTO;
import org.CMVD.Softwork.Fileshield.Model.ArchivoCorreo;
import org.CMVD.Softwork.Fileshield.Model.EnlaceSeguro;
import org.CMVD.Softwork.Fileshield.Servicios.CorreoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/correos")
@CrossOrigin(origins = "*")
public class CorreoController {
    @Autowired
    private CorreoService correoService;

    @Autowired
    private ArchivoCorreoRepositorio archivoCorreoRepositorio;

    @PostMapping("/enviar")
    public ResponseEntity<?> enviarCorreo(
            @RequestPart("correo") CorreoRequest correoRequest,
            @RequestPart(value = "adjuntos", required = false) List<MultipartFile> adjuntos) {
        try {
            EnlaceSeguro enlace = correoService.enviarCorreoConCifrado(
                    correoRequest,
                    adjuntos != null ? adjuntos : Collections.emptyList());
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("token", enlace.getTokenUnico());
            respuesta.put("urlEnlaceSeguro", "http://localhost:8080/api/enlaces/" + enlace.getTokenUnico() + "/validar");
            return ResponseEntity.ok(respuesta);
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

    @PostMapping("/adjuntos/descifrar/{idAdjunto}")
    public ResponseEntity<byte[]> descifrarAdjunto(@PathVariable Integer idAdjunto, @RequestBody Map<String, String> requestBody) {
        try {
            String claveBase64 = requestBody.get("claveBase64");
            if (claveBase64 == null || claveBase64.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
            byte[] descifrado = correoService.descifrarAdjuntoCorreo(idAdjunto, claveBase64);

            String filename = archivoCorreoRepositorio.findById(idAdjunto)
                    .map(ArchivoCorreo::getNombreOriginal)
                    .orElse("descifrado");
            MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(contentType)
                    .body(descifrado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
