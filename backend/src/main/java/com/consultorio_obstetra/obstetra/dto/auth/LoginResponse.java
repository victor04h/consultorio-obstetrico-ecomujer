package com.consultorio_obstetra.obstetra.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String correo;
    private String nombreCompleto;
    private String rol;
    /** Modulos del dashboard a los que este perfil tiene acceso (ej. "CITAS", "PACIENTES"). */
    private List<String> permisos;
}
