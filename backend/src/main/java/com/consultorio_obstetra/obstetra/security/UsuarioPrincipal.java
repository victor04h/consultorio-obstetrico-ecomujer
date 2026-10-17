package com.consultorio_obstetra.obstetra.security;

import com.consultorio_obstetra.obstetra.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador de Usuario (entidad) a UserDetails (Spring Security).
 * El rol viene del nombre del Perfil, ej. "Administrador" -> ROLE_ADMINISTRADOR.
 * Ademas, cada Permiso marcado en el Perfil se expone como una autoridad
 * "PERM_<MODULO>_<ACCION>" (ej. PERM_CITAS_ACCESO), para que los controladores
 * puedan restringir cada seccion del panel con @PreAuthorize sin depender
 * unicamente del rol.
 */
public class UsuarioPrincipal implements UserDetails {

    private final Usuario usuario;

    public UsuarioPrincipal(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        String rol = usuario.getPerfil().getNombre().trim().toUpperCase().replace(" ", "_");
        authorities.add(new SimpleGrantedAuthority("ROLE_" + rol));

        usuario.getPerfil().getPermisos().forEach(permiso -> {
            String modulo = permiso.getModulo().trim().toUpperCase().replace(" ", "_");
            String accion = permiso.getAccion().trim().toUpperCase().replace(" ", "_");
            authorities.add(new SimpleGrantedAuthority("PERM_" + modulo + "_" + accion));
        });

        return authorities;
    }

    @Override
    public String getPassword() {
        return usuario.getContrasenaHash();
    }

    @Override
    public String getUsername() {
        return usuario.getCorreo();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return usuario.getBloqueadoHasta() == null
                || usuario.getBloqueadoHasta().isBefore(java.time.LocalDateTime.now());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuario.getEstado() == com.consultorio_obstetra.obstetra.model.enums.EstadoActivo.ACTIVO;
    }
}
