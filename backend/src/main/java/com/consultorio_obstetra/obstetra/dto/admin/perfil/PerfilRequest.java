package com.consultorio_obstetra.obstetra.dto.admin.perfil;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class PerfilRequest {

    @NotBlank(message = "El nombre del perfil es obligatorio")
    private String nombre;

    private String descripcion;

    /** IDs de Permiso asignados a este perfil (puede venir vacio). */
    private Set<Long> permisoIds;
}
