package com.consultorio_obstetra.obstetra.dto.registro;

import com.consultorio_obstetra.obstetra.service.externo.PersonaExternaDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Resultado de "¿Los datos del paciente vienen de una fuente externa (API)?".
 * Si encontrado=false, el front debe mostrar el formulario en blanco para
 * que el paciente lo complete manualmente (rama del BPM sin API externa).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDniResponse {
    private boolean encontrado;
    private PersonaExternaDTO persona;
    private boolean menorDeEdadEstimado;
}
