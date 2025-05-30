package org.CMVD.Softwork.Fileshield.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class ArchivoCorreo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idArchivoCorreo;

    private String nombreOriginal;
    private String rutaCifrado;

    @ManyToOne
    private Correo correo;
}
