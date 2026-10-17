package com.consultorio_obstetra.obstetra.service.externo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Datos que devolvería una fuente externa de identidad (por ejemplo un
 * registro civil / RENIEC) al consultar por numero de documento, tal como
 * lo pide el proceso de Registro del BPM ("Consultar API externa y traer
 * los datos del paciente").
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaExternaDTO {
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
}
