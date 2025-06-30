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
    @Lob
    @Column(name = "contenido_cifrado", columnDefinition = "MEDIUMBLOB")
    private byte[] contenidoCifrado;

    @ManyToOne
    private Correo correo;
}
