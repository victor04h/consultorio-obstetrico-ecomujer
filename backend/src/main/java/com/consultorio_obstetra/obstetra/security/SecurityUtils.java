package com.consultorio_obstetra.obstetra.security;

import com.consultorio_obstetra.obstetra.model.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Acceso al Usuario autenticado actual a partir del contexto de seguridad
 * (poblado por JwtAuthenticationFilter), util para registrar auditoria.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<Usuario> usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioPrincipal principal)) {
            return Optional.empty();
        }
        return Optional.of(principal.getUsuario());
    }
}
