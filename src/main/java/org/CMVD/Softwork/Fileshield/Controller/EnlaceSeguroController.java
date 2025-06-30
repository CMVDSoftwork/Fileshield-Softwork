package org.CMVD.Softwork.Fileshield.Controller;

import org.CMVD.Softwork.Fileshield.DTO.Correo.CorreoDTO;
import org.CMVD.Softwork.Fileshield.Model.Correo;
import org.CMVD.Softwork.Fileshield.Model.EnlaceSeguro;
import org.CMVD.Softwork.Fileshield.Model.RecepcionCorreo;
import org.CMVD.Softwork.Fileshield.Repositorios.CorreoRepositorio;
import org.CMVD.Softwork.Fileshield.Repositorios.EnlaceSeguroRepositorio;
import org.CMVD.Softwork.Fileshield.Repositorios.RecepcionCorreoRepositorio;
import org.CMVD.Softwork.Fileshield.Servicios.EnlaceSeguroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/enlaces")
public class EnlaceSeguroController {
    @Autowired
    private EnlaceSeguroService enlaceSeguroService;

    @Autowired
    private EnlaceSeguroRepositorio RepoEnlaceSeguro;

    @GetMapping("/{token}/validar")
    public ResponseEntity<String> validarTokenAntesDeLogin(@PathVariable String token) {
        Optional<EnlaceSeguro> optional = RepoEnlaceSeguro.findByTokenUnico(token);

        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                    .body("<h2 style='color:red;'> Enlace no encontrado</h2><p>Verifica que el enlace sea correcto.</p>");
        }

        EnlaceSeguro enlace = optional.get();

        if (enlace.isUsado()) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                    .body("<h2 style='color:orange;'>️ Enlace ya utilizado</h2><p>Este enlace ya fue usado para recuperar una clave.</p>");
        }

        if (enlace.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                    .body("<h2 style='color:gray;'> Enlace expirado</h2><p>Este enlace ha caducado. Solicita uno nuevo desde la aplicación.</p>");
        }

        String html = """
<html>
<head>
    <meta charset="UTF-8" />
    <title>FileShield | Enlace verificado</title>
    <style>
        body {
            background-color: #0B0F1A;
            color: #FFFFFF;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .container {
            background: #12172B;
            padding: 40px 30px;
            border-radius: 12px;
            box-shadow: 0 8px 24px rgba(30, 144, 255, 0.2);
            max-width: 500px;
            width: 100%;
            text-align: center;
        }
        .logo {
            width: 120px;
            margin-bottom: 25px;
            filter: drop-shadow(0 0 2px #1E90FF);
        }
        h2 {
            color: #1E90FF;
            margin-bottom: 20px;
            font-weight: 700;
            font-size: 1.8rem;
        }
        p {
            color: #CCCCCC;
            font-size: 1rem;
            margin-bottom: 20px;
        }
        ol {
            text-align: left;
            margin: 0 0 20px 20px;
            color: #AAAAAA;
            font-size: 0.95rem;
            line-height: 1.5;
        }
        ol li {
            margin-bottom: 8px;
        }
        .footer {
            color: #666666;
            font-size: 0.8rem;
            margin-top: 30px;
        }
    
    </style>
</head>
<body>
    <div class="container">
        <img src="/api/enlaces/logo" alt="Logo FileShield" class="logo" />
        <h2>🔐 Enlace Seguro Verificado</h2>
        <p>Este enlace ha sido validado correctamente.</p>

        <p><strong>Pasos para ver la clave de descifrado:</strong></p>
        <ol>
            <li>Abre la aplicación <strong>FileShield</strong>.</li>
            <li>Inicia sesión con el correo <strong>receptor</strong>.</li>
            <li>Ve a la sección <strong>“Ver clave”</strong>.</li>
            <li>Pega este mismo enlace cuando se te solicite.</li>
        </ol>

        <p class="footer">Este enlace expira en 24 horas y solo puede usarse una vez.</p>
    </div>
</body>
</html>
""";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                .body(html);
    }

    @GetMapping("/logo")
    public ResponseEntity<Resource> logo() throws IOException {
        Resource resource = new ClassPathResource("static/Logo.svg");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/svg+xml"))
                .body(resource);
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> accederClave(@PathVariable String token, @RequestParam String correoUsuario) {
        Optional<CorreoDTO> optionalCorreoDTO = enlaceSeguroService.validarToken(token, correoUsuario);

        if (optionalCorreoDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token inválido, expirado o acceso no autorizado para este correo.");
        }
        CorreoDTO correoDTO = optionalCorreoDTO.get();
        return ResponseEntity.ok(correoDTO);
    }
}
