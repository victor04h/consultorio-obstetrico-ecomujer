package com.consultorio_obstetra.obstetra.service;

import com.consultorio_obstetra.obstetra.dto.auth.LoginRequest;
import com.consultorio_obstetra.obstetra.dto.auth.LoginResponse;
import com.consultorio_obstetra.obstetra.exception.ApiException;
import com.consultorio_obstetra.obstetra.model.Usuario;
import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import com.consultorio_obstetra.obstetra.repository.UsuarioRepository;
import com.consultorio_obstetra.obstetra.security.JwtService;
import com.consultorio_obstetra.obstetra.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementa el proceso "Login" del BPM: valida credenciales, bloquea
 * temporalmente tras varios intentos fallidos (RF-05) y redirige segun el rol.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_INTENTOS_FALLIDOS = 5;
    private static final int MINUTOS_BLOQUEO = 15;

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new BadCredentialsException("Correo o contraseña incorrectos"));

        if (usuario.getBloqueadoHasta() != null && usuario.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.LOCKED,
                    "Cuenta bloqueada temporalmente por intentos fallidos. Intenta de nuevo más tarde.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getContrasena())
            );
        } catch (org.springframework.security.core.AuthenticationException ex) {
            registrarIntentoFallido(usuario);
            throw new BadCredentialsException("Correo o contraseña incorrectos");
        }

        if (usuario.getEstado() == EstadoActivo.INACTIVO) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Esta cuenta está inactiva");
        }

        if (!usuario.isCorreoValidado()) {
            throw new ApiException(HttpStatus.FORBIDDEN,
                    "Debes validar tu correo antes de iniciar sesión. Revisa tu bandeja de entrada.");
        }

        usuario.setIntentosFallidos(0);
        usuario.setBloqueadoHasta(null);
        usuarioRepository.save(usuario);

        UsuarioPrincipal principal = new UsuarioPrincipal(usuario);
        String token = jwtService.generarToken(principal);

        List<String> permisos = usuario.getPerfil().getPermisos().stream()
                .map(p -> p.getModulo().trim().toUpperCase())
                .distinct()
                .toList();

        return LoginResponse.builder()
                .token(token)
                .correo(usuario.getCorreo())
                .nombreCompleto(usuario.getPersona().getNombres() + " " + usuario.getPersona().getApellidos())
                .rol(usuario.getPerfil().getNombre())
                .permisos(permisos)
                .build();
    }

    private void registrarIntentoFallido(Usuario usuario) {
        int intentos = usuario.getIntentosFallidos() + 1;
        usuario.setIntentosFallidos(intentos);
        if (intentos >= MAX_INTENTOS_FALLIDOS) {
            usuario.setBloqueadoHasta(LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEO));
        }
        usuarioRepository.save(usuario);
    }
}
