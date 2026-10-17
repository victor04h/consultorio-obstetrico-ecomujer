package com.consultorio_obstetra.obstetra.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Respuesta generica de confirmacion ({"mensaje": "..."}) para operaciones
 * que no necesitan devolver un recurso (activar/desactivar, validar correo,
 * eliminar, etc.).
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MensajeResponse {
    private String mensaje;

    public static MensajeResponse de(String mensaje) {
        return new MensajeResponse(mensaje);
    }
}
