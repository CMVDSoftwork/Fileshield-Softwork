package org.CMVD.Softwork.Fileshield.Controller;

import org.CMVD.Softwork.Fileshield.DTO.UsuarioDTO;
import org.CMVD.Softwork.Fileshield.Servicios.UsuarioService;
import org.CMVD.Softwork.Fileshield.SessionRequest.LoginRequest;
import org.CMVD.Softwork.Fileshield.SessionRequest.LoginResponse;
import org.CMVD.Softwork.Fileshield.SessionRequest.RegistroRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}

