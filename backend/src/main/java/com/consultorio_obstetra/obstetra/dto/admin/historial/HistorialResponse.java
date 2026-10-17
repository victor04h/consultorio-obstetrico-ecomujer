package com.consultorio_obstetra.obstetra.dto.admin.historial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialResponse {
    private Long id;
    private Long idPaciente;
    private String nombrePaciente;
    private Long idCita;
    private Long idProfesional;
    private String nombreProfesional;
    private String diagnostico;
    private String tratamiento;
    private String notas;
    private LocalDateTime fechaRegistro;
}
