package com.consultorio_obstetra.obstetra.dto.registro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Datos de una persona (paciente o apoderado) capturados en el formulario
 * de registro, ya sea traidos de la API externa o completados a mano.
 */
@Getter
@Setter
public class PersonaDatosDTO {

    @NotBlank(message = "El numero de documento es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El numero de documento debe tener 8 digitos")
    private String numeroDocumento;

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    private LocalDate fechaNacimiento;

    private String telefono;
}
