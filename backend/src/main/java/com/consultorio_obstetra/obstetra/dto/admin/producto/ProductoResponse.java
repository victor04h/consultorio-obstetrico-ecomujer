package com.consultorio_obstetra.obstetra.dto.admin.producto;

import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {
    private Long id;
    private Long idCategoria;
    private String nombreCategoria;
    private String nombre;
    private String descripcionCorta;
    private BigDecimal precio;
    private Integer stock;
    private boolean disponible;
    private EstadoActivo estado;
    private List<String> profesionales;
}
