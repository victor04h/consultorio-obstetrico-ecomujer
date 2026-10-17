package com.consultorio_obstetra.obstetra.dto.registro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroResponse {
    private Long idPaciente;
    private String correo;
    private String nombreCompleto;
    private boolean esMenorEdad;
    private String mensaje;
}
