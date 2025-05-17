package org.CMVD.Softwork.Fileshield.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class CarpetaMonitorizada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCarpetaMonitorizada;
    private String ruta;

    @ManyToOne
    private Usuario usuario;
}
