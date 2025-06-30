package org.CMVD.Softwork.Fileshield.Servicios.Impl;

import org.CMVD.Softwork.Fileshield.Servicios.Impl.UserContextService;
import org.CMVD.Softwork.Fileshield.DTO.Carpeta.ArchivoDTO;
import org.CMVD.Softwork.Fileshield.Model.Archivo;
import org.CMVD.Softwork.Fileshield.Model.CarpetaMonitorizada;
import org.CMVD.Softwork.Fileshield.Repositorios.ArchivoRepositorio;
import org.CMVD.Softwork.Fileshield.Repositorios.CarpetaMonitorizadaRepositorio;
import org.CMVD.Softwork.Fileshield.Servicios.CifradorAESService;
import org.CMVD.Softwork.Fileshield.Servicios.ArchivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ArchivoServiceImpl implements ArchivoService {
    @Autowired
    private ArchivoRepositorio archivoRepo;

    @Autowired
    private CarpetaMonitorizadaRepositorio carpetaRepo;

    @Autowired
    private CifradorAESService cifradorAESService;

    @Autowired
    private UserContextService userContextService;

    @Override
    public List<ArchivoDTO> obtenerArchivosPorCarpeta(Integer idCarpetaMonitorizada) {
        CarpetaMonitorizada carpeta = carpetaRepo.findById(idCarpetaMonitorizada)
                .orElseThrow(() -> new RuntimeException("Carpeta no encontrada"));

        Integer currentUserId = userContextService.getCurrentAuthenticatedUserId();
        if (currentUserId == null || carpeta.getUsuario() == null || !carpeta.getUsuario().getIdUsuario().equals(currentUserId)) {
            throw new AccessDeniedException("No tienes permiso para acceder a esta carpeta.");
        }

        List<Archivo> archivos = archivoRepo.findByCarpetaMonitorizada_IdCarpetaMonitorizada(idCarpetaMonitorizada);
        return archivos.stream()
                .map(ArchivoDTO::new)
                .collect(Collectors.toList());
    }


    @Override
    public List<ArchivoDTO> obtenerTodosLosArchivos() {
        Integer idUsuarioActual = userContextService.getCurrentAuthenticatedUserId();

        List<Archivo> archivos = archivoRepo.findByUsuario_IdUsuario(idUsuarioActual);

        return archivos.stream()
                .map(ArchivoDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] descifrarArchivo(Integer idArchivo, String claveBase64) throws IOException, GeneralSecurityException {
        Archivo archivo = archivoRepo.findById(idArchivo)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

        Integer currentUserId = userContextService.getCurrentAuthenticatedUserId();
        if (currentUserId == null || archivo.getUsuario() == null || !archivo.getUsuario().getIdUsuario().equals(currentUserId)) {
            throw new AccessDeniedException("No tienes permiso para realizar esta operación.");
        }

        SecretKey clave = cifradorAESService.bytesAClave(Base64.getDecoder().decode(claveBase64));
        File archivoCifrado = new File(archivo.getRutaArchivo());

        String nombreOriginalSinExtCifrada = archivo.getNombreArchivo().replace(".enc", "");
        String rutaDescifradoTemp = System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString() + "_" + nombreOriginalSinExtCifrada;
        File archivoDescifradoTemp = new File(rutaDescifradoTemp);

        cifradorAESService.descifrarArchivo(archivoCifrado, archivoDescifradoTemp, clave);
        byte[] descifradoBytes = Files.readAllBytes(archivoDescifradoTemp.toPath());

        archivo.setEstado("DESCIFRADO");
        archivoRepo.save(archivo);

        if (archivoDescifradoTemp.exists()) {
            if (!archivoDescifradoTemp.delete()) {
                System.err.println("Advertencia: No se pudo eliminar el archivo descifrado temporal: " + archivoDescifradoTemp.getName());
            }
        }
        return descifradoBytes;
    }

    @Override
    public void eliminarArchivo(Integer idArchivo) {
        Archivo archivo = archivoRepo.findById(idArchivo).orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

        Integer currentUserId = userContextService.getCurrentAuthenticatedUserId();
        if (currentUserId == null || archivo.getUsuario() == null || !archivo.getUsuario().getIdUsuario().equals(currentUserId)) {
            throw new AccessDeniedException("No tienes permiso para eliminar este archivo.");
        }

        File archivoFisico = new File(archivo.getRutaArchivo());
        if (archivoFisico.exists()) {
            if (!archivoFisico.delete()) {
                System.err.println("Advertencia: No se pudo eliminar el archivo físico: " + archivoFisico.getName());
            }
        }
        archivoRepo.delete(archivo);
    }
}
