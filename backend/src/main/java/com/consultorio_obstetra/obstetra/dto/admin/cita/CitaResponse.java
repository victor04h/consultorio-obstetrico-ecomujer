package com.consultorio_obstetra.obstetra.dto.admin.cita;

import com.consultorio_obstetra.obstetra.model.enums.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitaResponse {
    private Long id;
    private Long idPaciente;
    private String nombrePaciente;
    private Long idProducto;
    private String nombreProducto;
    private Long idProfesional;
    private String nombreProfesional;
    private LocalDateTime fechaHora;
    private String motivoCita;
    private EstadoCita estado;
}
