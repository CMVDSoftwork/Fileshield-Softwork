package org.CMVD.Softwork.Fileshield.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Entity
public class Archivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idArchivo;
    private String nombreArchivo, estado, tipoArchivo;
    private int tamaño;
    private Date fechaSubida;

    @ManyToOne
    private Usuario usuario;
    @ManyToOne
    private CarpetaMonitorizada carpetaMonitorizada;

}
