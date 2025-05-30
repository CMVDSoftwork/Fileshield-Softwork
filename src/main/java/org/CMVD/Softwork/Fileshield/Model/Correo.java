package org.CMVD.Softwork.Fileshield.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Correo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCorreo;
    private String contenidoCifrado, claveCifDes, estatus;

    @OneToOne(mappedBy = "correo")
    private EnvioCorreo envioCorreo;
}
