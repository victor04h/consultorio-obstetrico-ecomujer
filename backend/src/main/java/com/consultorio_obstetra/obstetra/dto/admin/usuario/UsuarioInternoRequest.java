package com.consultorio_obstetra.obstetra.dto.admin.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Alta de una cuenta de acceso al panel para personal interno (Secretaria,
 * Obstetra, u otro Administrador). Los pacientes NO se crean por aqui: ellos
 * se registran solos desde el proceso publico de Registro.
 */
@Getter
@Setter
public class UsuarioInternoRequest {

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String correo;

    @NotBlank(message = "La contraseña temporal es obligatoria")
    private String contrasena;

    @NotNull(message = "El perfil es obligatorio")
    private Long idPerfil;

    /**
     * Solo aplica cuando el perfil elegido es "Obstetra": a que Profesional
     * (ya dado de alta en el modulo de Profesionales) corresponde este login,
     * para poder filtrar sus propias citas e historial clinico.
     */
    private Long idProfesional;
}
