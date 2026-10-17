package com.consultorio_obstetra.obstetra.dto.registro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsultaDniRequest {

    @NotBlank(message = "El numero de documento es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El numero de documento debe tener 8 digitos")
    private String numeroDocumento;
}
