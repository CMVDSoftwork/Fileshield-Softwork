package org.CMVD.Softwork.Fileshield.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Entity
public class RecepcionCorreo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRecepcionCorreo;
    private Date fechaRecepcion;

    @ManyToOne
    private Usuario usuarioRecepcion;
    @ManyToOne
    private EnvioCorreo envioRecepcion;
}
