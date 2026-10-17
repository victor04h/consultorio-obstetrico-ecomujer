package com.consultorio_obstetra.obstetra.dto.admin.historial;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistorialRequest {

    @NotNull(message = "El paciente es obligatorio")
    private Long idPaciente;

    private Long idCita;

    @NotNull(message = "El profesional es obligatorio")
    private Long idProfesional;

    private String diagnostico;

    private String tratamiento;

    private String notas;
}
