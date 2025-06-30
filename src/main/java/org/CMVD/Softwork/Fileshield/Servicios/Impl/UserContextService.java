package org.CMVD.Softwork.Fileshield.Servicios.Impl;

import org.CMVD.Softwork.Fileshield.Model.Usuario;
import org.CMVD.Softwork.Fileshield.Repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserContextService {
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    public Usuario getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("Acceso denegado. No hay usuario autenticado.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof String) {
            String correo = (String) principal;
            return usuarioRepositorio.findByCorreo(correo)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado para el correo: " + correo));
        }

        throw new IllegalStateException("Tipo de principal inesperado en el contexto de seguridad: " + principal.getClass().getName());
    }

    public Integer getCurrentAuthenticatedUserId() {
        Usuario currentUser = getCurrentAuthenticatedUser();
        return currentUser.getIdUsuario();
    }
}
