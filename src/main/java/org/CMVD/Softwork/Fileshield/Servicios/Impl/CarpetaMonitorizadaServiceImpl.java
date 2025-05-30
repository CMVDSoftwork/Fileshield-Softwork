package org.CMVD.Softwork.Fileshield.Servicios.Impl;

import org.CMVD.Softwork.Fileshield.DTO.CarpetaMonitorizadaDTO;
import org.CMVD.Softwork.Fileshield.Model.CarpetaMonitorizada;
import org.CMVD.Softwork.Fileshield.Model.Usuario;
import org.CMVD.Softwork.Fileshield.Repositorios.CarpetaMonitorizadaRepositorio;
import org.CMVD.Softwork.Fileshield.Repositorios.UsuarioRepositorio;
import org.CMVD.Softwork.Fileshield.Servicios.CarpetaMonitorizadaService;
import org.CMVD.Softwork.Fileshield.Servicios.CifradorAESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class CarpetaMonitorizadaServiceImpl implements CarpetaMonitorizadaService {
    @Autowired
    private CarpetaMonitorizadaRepositorio RepoCarpeta;
    @Autowired
    private UsuarioRepositorio RepoUsuario;
    @Autowired
    private CifradorAESService cifradorAESService;

    private final ExecutorService executorService = Executors.newCachedThreadPool();


    @Override
    public CarpetaMonitorizadaDTO registrarCarpeta(CarpetaMonitorizadaDTO carpetaDTO) {
        CarpetaMonitorizada carpeta = new CarpetaMonitorizada();
        carpeta.setRuta(carpetaDTO.getRuta());

        Usuario usuario = RepoUsuario.findById(carpetaDTO.getUsuarioDTO().getIdUsuario()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        carpeta.setUsuario(usuario);
        CarpetaMonitorizada guardada = RepoCarpeta.save(carpeta);

        return new CarpetaMonitorizadaDTO(guardada);
    }

    @Override
    public List<CarpetaMonitorizadaDTO> obtenerCarpetasPorUsuario(Integer idUsuario) {
        return RepoCarpeta.findByUsuarioIdUsuario(idUsuario)
                .stream()
                .map(CarpetaMonitorizadaDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarCarpeta(Integer idCarpeta) {
        RepoCarpeta.deleteById(idCarpeta);
    }

    @Override
    public void iniciarMonitoreo(String ruta, SecretKey clave) {
        executorService.submit(() -> {
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                Path path = Paths.get(ruta);
                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

                while (true) {
                    WatchKey key = watchService.take();

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> tipo = event.kind();

                        if (tipo == StandardWatchEventKinds.ENTRY_CREATE) {
                            Path archivoDetectado = path.resolve((Path) event.context());
                            File archivoOriginal = archivoDetectado.toFile();

                            if (archivoOriginal.isFile()) {
                                File archivoCifrado = new File(archivoOriginal.getAbsolutePath() + ".enc");

                                cifradorAESService.cifrarArchivo(archivoOriginal, archivoCifrado, clave);

                                if (archivoOriginal.delete()) {
                                    archivoCifrado.renameTo(archivoOriginal);
                                }
                            }
                        }
                    }
                    boolean valido = key.reset();
                    if (!valido) break;
                }

            } catch (IOException | InterruptedException | GeneralSecurityException e) {
                e.printStackTrace();
            }
        });
    }
}

