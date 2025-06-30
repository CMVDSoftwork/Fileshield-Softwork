package org.CMVD.Softwork.Fileshield.Servicios.Impl;

import org.CMVD.Softwork.Fileshield.DTO.Carpeta.CarpetaMonitorizadaDTO;
import org.CMVD.Softwork.Fileshield.Model.Archivo;
import org.CMVD.Softwork.Fileshield.Model.CarpetaMonitorizada;
import org.CMVD.Softwork.Fileshield.Model.Usuario;
import org.CMVD.Softwork.Fileshield.Repositorios.ArchivoRepositorio;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class CarpetaMonitorizadaServiceImpl implements CarpetaMonitorizadaService {
    @Autowired
    private CarpetaMonitorizadaRepositorio RepoCarpeta;
    @Autowired
    private UsuarioRepositorio RepoUsuario;
    @Autowired
    private CifradorAESService cifradorAESService;
    @Autowired
    private ArchivoRepositorio archivoRepo;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<String, Future<?>> carpetasMonitoreadas = new ConcurrentHashMap<>();

    @Override
    public CarpetaMonitorizadaDTO registrarCarpeta(CarpetaMonitorizadaDTO carpetaDTO) {
        if (carpetaDTO.getUsuarioDTO() == null) {
            throw new IllegalArgumentException("El usuario asociado a la carpeta es nulo");
        }

        Usuario usuario = RepoUsuario.findById(carpetaDTO.getUsuarioDTO().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Optional<CarpetaMonitorizada> existente = RepoCarpeta.findCarpetaMonitorizadaByRutaAndUsuario(
                carpetaDTO.getRuta(), usuario);

        if (existente.isPresent()) {
            return new CarpetaMonitorizadaDTO(existente.get());
        }

        CarpetaMonitorizada carpeta = new CarpetaMonitorizada();
        carpeta.setRuta(carpetaDTO.getRuta());
        carpeta.setUsuario(usuario);
        CarpetaMonitorizada guardada = RepoCarpeta.save(carpeta);

        return new CarpetaMonitorizadaDTO(guardada);
    }

    @Override
    public List<CarpetaMonitorizadaDTO> obtenerCarpetasPorUsuario(Integer idUsuario) {
        return RepoCarpeta.findByUsuarioIdUsuario(idUsuario)
                .stream()
                .map(carpeta -> {
                    CarpetaMonitorizadaDTO dto = new CarpetaMonitorizadaDTO(carpeta);
                    dto.setEstado(verificarEstado(dto.getRuta()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarCarpeta(Integer idCarpeta) {
        RepoCarpeta.deleteById(idCarpeta);
    }

    @Override
    public void iniciarMonitoreo(String ruta, SecretKey clave) {
        System.out.println("Iniciando monitoreo de la carpeta: " + ruta);

        Future<?> future = executorService.submit(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

                Path path = Paths.get(ruta);
                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
                System.out.println("Monitoreo establecido correctamente. Esperando cambios...");

                Map<String, Long> archivosEnProceso = new ConcurrentHashMap<>();

                CarpetaMonitorizada carpetaMonitorizada = RepoCarpeta.findCarpetaMonitorizadaByRuta(ruta)
                        .orElseThrow(() -> new RuntimeException("Carpeta no encontrada en DB: " + ruta));
                Usuario usuario = carpetaMonitorizada.getUsuario();

                while (!Thread.currentThread().isInterrupted()) {
                    WatchKey key = watchService.take();

                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.OVERFLOW) continue;

                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            Path archivoDetectado = path.resolve((Path) event.context());
                            File archivoOriginal = archivoDetectado.toFile();
                            String nombreArchivo = archivoOriginal.getName();

                            Long ultimoProcesamiento = archivosEnProceso.get(nombreArchivo);
                            long tiempoActual = System.currentTimeMillis();

                            if (nombreArchivo.endsWith(".enc") ||
                                    (ultimoProcesamiento != null && tiempoActual - ultimoProcesamiento < 5000)) {
                                continue;
                            }

                            archivosEnProceso.put(nombreArchivo, tiempoActual);
                            System.out.println("Nuevo archivo detectado: " + nombreArchivo);

                            if (archivoOriginal.isFile()) {
                                try {
                                    Thread.sleep(2000);

                                    if (!archivoOriginal.exists()) continue;
                                    if (!archivoOriginal.canRead() || !archivoOriginal.canWrite()) {
                                        System.out.println("No se puede acceder al archivo: " + nombreArchivo);
                                        continue;
                                    }

                                    File archivoCifrado = new File(archivoOriginal.getAbsolutePath() + ".enc");
                                    System.out.println("Iniciando proceso de cifrado para: " + nombreArchivo);

                                    cifradorAESService.cifrarArchivo(archivoOriginal, archivoCifrado, clave);

                                    if (archivoCifrado.exists()) {
                                        if (archivoOriginal.delete()) {
                                            System.out.println("Archivo original eliminado: " + nombreArchivo);
                                        } else {
                                            System.out.println("Error al eliminar el archivo original: " + nombreArchivo);
                                        }

                                        Archivo archivoEntity = new Archivo();
                                        archivoEntity.setNombreArchivo(archivoCifrado.getName());
                                        archivoEntity.setRutaArchivo(archivoCifrado.getAbsolutePath());
                                        archivoEntity.setTipoArchivo("cifrado");
                                        archivoEntity.setEstado("CIFRADO");
                                        archivoEntity.setTamaño((int) archivoCifrado.length());
                                        archivoEntity.setFechaSubida(new Date());
                                        archivoEntity.setUsuario(usuario);
                                        archivoEntity.setCarpetaMonitorizada(carpetaMonitorizada);

                                        archivoRepo.save(archivoEntity);
                                        System.out.println("Archivo cifrado guardado en DB: " + archivoCifrado.getName());
                                    } else {
                                        System.out.println("No se creó el archivo cifrado: " + archivoCifrado.getName());
                                    }

                                } catch (InterruptedException e) {
                                    System.out.println("Proceso interrumpido para: " + nombreArchivo);
                                    Thread.currentThread().interrupt();
                                } catch (Exception ex) {
                                    System.out.println("Error procesando archivo: " + ex.getMessage());
                                    ex.printStackTrace();
                                } finally {
                                    archivosEnProceso.remove(nombreArchivo);
                                }
                            }
                        }
                    }

                    boolean valido = key.reset();
                    if (!valido) {
                        System.out.println("El monitoreo de la carpeta ha sido interrumpido");
                        break;
                    }
                }

            } catch (IOException | InterruptedException e) {
                if (e instanceof InterruptedException) {
                    System.out.println("Monitoreo interrumpido manualmente para: " + ruta);
                    Thread.currentThread().interrupt();
                } else {
                    System.out.println("Error en el monitoreo: " + e.getMessage());
                    e.printStackTrace();
                }
            } finally {
                carpetasMonitoreadas.remove(ruta);
            }
        });

        carpetasMonitoreadas.put(ruta, future);
    }

    public String verificarEstado(String ruta) {
        return carpetasMonitoreadas.containsKey(ruta) ? "EN MONITOREO" : "INACTIVO";
    }

    @Override
    public void detenerMonitoreo(String ruta) {
        Future<?> future = carpetasMonitoreadas.get(ruta);
        if (future != null) {
            future.cancel(true);
            carpetasMonitoreadas.remove(ruta);
            System.out.println("Monitoreo detenido para la carpeta: " + ruta);
        } else {
            System.out.println("No se encontró monitoreo activo para la carpeta: " + ruta);
        }
    }
}

