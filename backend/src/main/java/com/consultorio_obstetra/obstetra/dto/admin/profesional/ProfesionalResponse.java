package com.consultorio_obstetra.obstetra.dto.admin.profesional;

import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfesionalResponse {
    private Long id;
    private String nombres;
    private String apellidos;
    private String especialidad;
    private Integer añosExperiencia;
    private String reseña;
    private String fotoUrl;
    private EstadoActivo estado;
    private Double calificacionPromedio;
}
