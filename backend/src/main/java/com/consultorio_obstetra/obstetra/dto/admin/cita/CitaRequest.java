package com.consultorio_obstetra.obstetra.dto.admin.cita;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CitaRequest {

    @NotNull(message = "El paciente es obligatorio")
    private Long idPaciente;

    @NotNull(message = "El producto/servicio es obligatorio")
    private Long idProducto;

    @NotNull(message = "El profesional es obligatorio")
    private Long idProfesional;

    @NotNull(message = "La fecha y hora son obligatorias")
    private LocalDateTime fechaHora;

    @Size(max = 300, message = "El motivo de la cita no puede superar los 300 caracteres")
    private String motivoCita;
}
