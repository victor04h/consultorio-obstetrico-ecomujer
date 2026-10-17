package com.consultorio_obstetra.obstetra.dto.admin.profesional;

import com.consultorio_obstetra.obstetra.model.enums.EstadoDisponibilidad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadResponse {
    private Long id;
    private Long idProfesional;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstadoDisponibilidad estado;
}
