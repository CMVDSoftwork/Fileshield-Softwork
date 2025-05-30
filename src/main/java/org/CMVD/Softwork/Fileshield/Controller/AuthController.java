package org.CMVD.Softwork.Fileshield.Controller;

import org.CMVD.Softwork.Fileshield.DTO.SessionRequest.CambiarContraseñaRequest;
import org.CMVD.Softwork.Fileshield.DTO.UsuarioDTO;
import org.CMVD.Softwork.Fileshield.Servicios.UsuarioService;
import org.CMVD.Softwork.Fileshield.DTO.SessionRequest.LoginRequest;
import org.CMVD.Softwork.Fileshield.DTO.SessionRequest.LoginResponse;
import org.CMVD.Softwork.Fileshield.DTO.SessionRequest.RegistroRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UsuarioService ServeUsuario;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registrar(@RequestBody RegistroRequest request) {
        return ResponseEntity.ok(ServeUsuario.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(ServeUsuario.login(request));
    }

    @PostMapping("/cambiar-contrasena")
    public ResponseEntity<String> cambiarContrasena(@RequestBody CambiarContraseñaRequest request, Authentication authentication) {
        String correo = authentication.getName();
        ServeUsuario.cambiarContrasena(correo, request.getContrasenaActual(), request.getNuevaContrasena());
        return ResponseEntity.ok("Contraseña actualizada correctamente.");
    }
}

