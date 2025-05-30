package org.CMVD.Softwork.Fileshield.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class EnlaceSeguro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEnlaceSeguro;
    private String tokenUnico, claveCifradoBase64;
    private LocalDateTime fechaExpiracion;
    private boolean usado = false;
    @OneToOne
    private Correo correo;

    @ManyToOne
    private Usuario receptor;
}
