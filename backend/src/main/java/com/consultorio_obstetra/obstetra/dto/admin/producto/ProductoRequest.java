package com.consultorio_obstetra.obstetra.dto.admin.producto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class ProductoRequest {

    @NotNull(message = "La categoría es obligatoria")
    private Long idCategoria;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcionCorta;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    private BigDecimal precio;

    private Integer stock;

    private boolean disponible = true;

    /** IDs de Profesional que ofrecen este producto/servicio (opcional). */
    private Set<Long> profesionalIds;
}
