package org.CMVD.Softwork.Fileshield.Controller;

import jakarta.persistence.EntityNotFoundException;
import org.CMVD.Softwork.Fileshield.DTO.Carpeta.ArchivoDTO;
import org.CMVD.Softwork.Fileshield.DTO.DescifrarArchivoRequest;
import org.CMVD.Softwork.Fileshield.Model.Archivo;
import org.CMVD.Softwork.Fileshield.Repositorios.ArchivoRepositorio;
import org.CMVD.Softwork.Fileshield.Servicios.ArchivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {
    @Autowired
    private ArchivoService archivoService;
    @Autowired
    private ArchivoRepositorio archivoRepositorio;

    @GetMapping("/carpeta/{idCarpetaMonitorizada}")
    public ResponseEntity<List<ArchivoDTO>> obtenerArchivosPorCarpeta(@PathVariable Integer idCarpetaMonitorizada) {
        List<ArchivoDTO> archivos = archivoService.obtenerArchivosPorCarpeta(idCarpetaMonitorizada);
        return ResponseEntity.ok(archivos);
    }

    @PostMapping("/descifrar")
    public ResponseEntity<?> descifrarArchivo(@RequestBody DescifrarArchivoRequest request) {
        try {
            byte[] archivoDescifradoBytes = archivoService.descifrarArchivo(request.getIdArchivo(), request.getClavePersonal());

            Archivo archivo = archivoRepositorio.findById(request.getIdArchivo())
                    .orElseThrow(() -> new EntityNotFoundException("Archivo monitoreado no encontrado para ID: " + request.getIdArchivo()));
            String nombreOriginal = archivo.getNombreArchivo().replace(".enc", "");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreOriginal + "\"")
                    .body(archivoDescifradoBytes);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al descifrar el archivo: " + e.getMessage());
        }
    }

    @DeleteMapping("/{idArchivo}")
    public ResponseEntity<Void> eliminarArchivo(@PathVariable Integer idArchivo) {
        archivoService.eliminarArchivo(idArchivo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/todos")
    public ResponseEntity<List<ArchivoDTO>> obtenerTodosLosArchivos() {
        List<ArchivoDTO> archivos = archivoService.obtenerTodosLosArchivos();
        return ResponseEntity.ok(archivos);
    }
}
