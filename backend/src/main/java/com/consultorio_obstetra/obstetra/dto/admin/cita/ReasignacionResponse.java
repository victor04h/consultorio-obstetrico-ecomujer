package com.consultorio_obstetra.obstetra.dto.admin.cita;

import com.consultorio_obstetra.obstetra.model.enums.ResultadoReasignacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReasignacionResponse {
    private Long id;
    private Long idCita;
    private String nombrePaciente;
    private String nombreProfesional;
    private String nombreProducto;
    private LocalDateTime fechaHoraAnterior;
    private LocalDateTime fechaHoraNueva;
    private ResultadoReasignacion resultado;
    private String motivoRechazo;
    private LocalDateTime fechaSolicitud;
}
