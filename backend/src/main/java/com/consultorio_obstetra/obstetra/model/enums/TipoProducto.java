package com.consultorio_obstetra.obstetra.model.enums;

/**
 * Exigido por la columna `tipo` (NOT NULL) de la tabla `producto` en la base
 * de datos oficial del curso. El admin no lo elige a mano: se infiere
 * automaticamente a partir del nombre de la Categoria (ver
 * ProductoAdminService.inferirTipo).
 */
public enum TipoProducto {
    MEDICAMENTO,
    SERVICIO_ECOGRAFIA
}
