package com.consultorio_obstetra.obstetra.dto.admin.paciente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacienteResponse {
    private Long id;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String correo;
    private boolean esMenorEdad;
    private String nombreApoderado;
}
