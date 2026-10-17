package com.consultorio_obstetra.obstetra.dto.admin.profesional;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfesionalRequest {

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    private String especialidad;

    @Min(value = 0, message = "Los años de experiencia no pueden ser negativos")
    private Integer añosExperiencia;

    private String reseña;

    private String fotoUrl;
}
