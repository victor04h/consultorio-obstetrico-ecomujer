package com.consultorio_obstetra.obstetra.dto.admin.calificacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionResponse {
    private Long id;
    private Long idProfesional;
    private String nombreProfesional;
    private Long idPaciente;
    private String nombrePaciente;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime fechaCreacion;
}
