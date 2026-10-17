package com.consultorio_obstetra.obstetra.dto.common;

import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Cuerpo generico para activar/desactivar un registro (soft-delete: nunca
 * se borra de la base de datos, solo cambia de estado).
 */
@Getter
@Setter
public class CambiarEstadoRequest {

    @NotNull(message = "El estado es obligatorio")
    private EstadoActivo estado;
}
