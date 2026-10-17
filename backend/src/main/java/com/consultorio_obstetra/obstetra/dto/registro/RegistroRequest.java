package com.consultorio_obstetra.obstetra.dto.registro;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Cuerpo de "Completar formulario de registro". apoderado solo es
 * obligatorio cuando, segun la fecha de nacimiento del paciente, este
 * resulta ser menor de edad (se valida en RegistroService, no aqui, porque
 * depende del dato de otro campo).
 */
@Getter
@Setter
public class RegistroRequest {

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contrasena;

    @NotNull(message = "Los datos del paciente son obligatorios")
    @Valid
    private PersonaDatosDTO paciente;

    @Valid
    private PersonaDatosDTO apoderado;
}
