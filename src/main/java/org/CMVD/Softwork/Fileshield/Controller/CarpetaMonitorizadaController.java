package org.CMVD.Softwork.Fileshield.Controller;

import org.CMVD.Softwork.Fileshield.DTO.CarpetaMonitorizadaDTO;
import org.CMVD.Softwork.Fileshield.DTO.MonitoreoRequest;
import org.CMVD.Softwork.Fileshield.Servicios.CarpetaMonitorizadaService;
import org.CMVD.Softwork.Fileshield.Servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.crypto.SecretKey;
import java.util.List;

@RestController
@RequestMapping("/api/carpetas")
public class CarpetaMonitorizadaController {

    @Autowired
    private CarpetaMonitorizadaService carpetaService;
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrar")
    public ResponseEntity<CarpetaMonitorizadaDTO> registrarCarpeta(@RequestBody CarpetaMonitorizadaDTO carpetaDTO) {
        CarpetaMonitorizadaDTO nueva = carpetaService.registrarCarpeta(carpetaDTO);
        return ResponseEntity.ok(nueva);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<CarpetaMonitorizadaDTO>> obtenerCarpetasPorUsuario(@PathVariable Integer idUsuario) {
        List<CarpetaMonitorizadaDTO> carpetas = carpetaService.obtenerCarpetasPorUsuario(idUsuario);
        return ResponseEntity.ok(carpetas);
    }

    @DeleteMapping("/{idCarpetaMonirorizada}")
    public ResponseEntity<Void> eliminarCarpeta(@PathVariable Integer idCarpetaMonirorizada) {
        carpetaService.eliminarCarpeta(idCarpetaMonirorizada);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/monitorear")
    public ResponseEntity<String> iniciarMonitoreo(@RequestBody MonitoreoRequest request) {
        try {
            SecretKey clave = usuarioService.recuperarClaveAES(request.getIdUsuario(), request.getContrasena());
            carpetaService.iniciarMonitoreo(request.getRuta(), clave);
            return ResponseEntity.ok("Monitoreo iniciado exitosamente para la ruta: " + request.getRuta());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al iniciar monitoreo: " + e.getMessage());
        }
    }
}
