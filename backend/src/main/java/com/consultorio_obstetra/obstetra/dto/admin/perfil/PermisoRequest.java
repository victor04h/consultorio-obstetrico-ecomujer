package com.consultorio_obstetra.obstetra.dto.admin.perfil;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermisoRequest {

    @NotBlank(message = "El modulo es obligatorio")
    private String modulo;

    @NotBlank(message = "La accion es obligatoria")
    private String accion;

    private String descripcion;
}
