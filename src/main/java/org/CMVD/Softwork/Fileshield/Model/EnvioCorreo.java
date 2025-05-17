package org.CMVD.Softwork.Fileshield.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Entity
public class EnvioCorreo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEnvioCorreo;
    private Date fechaEnvio;

    @ManyToOne
    private Usuario usuarioEmisor;
    @ManyToOne
    private Correo correo;

}
