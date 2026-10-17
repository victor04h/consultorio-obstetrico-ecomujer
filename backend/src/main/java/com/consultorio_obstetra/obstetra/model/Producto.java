package com.consultorio_obstetra.obstetra.model;

import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Producto/servicio del catalogo: cubre tanto las ecografias (categoria
 * "Ecografias") como los medicamentos (categoria "Medicamentos"), segun
 * el proceso "Producto" del BPM.
 */
@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    /**
     * Exigido por la tabla `producto` de la base de datos (columna `tipo`,
     * NOT NULL): distingue medicamentos de servicios de ecografia. Se
     * calcula solo, no lo pide el formulario (ver ProductoAdminService).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private com.consultorio_obstetra.obstetra.model.enums.TipoProducto tipo =
            com.consultorio_obstetra.obstetra.model.enums.TipoProducto.MEDICAMENTO;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion_corta", length = 500)
    private String descripcionCorta;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal precio = BigDecimal.ZERO;

    @Column
    private Integer stock;

    @Builder.Default
    private boolean disponible = true;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoActivo estado = EstadoActivo.ACTIVO;

    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private LocalDateTime fechaModificacion;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "producto_profesional",
            joinColumns = @JoinColumn(name = "id_producto"),
            inverseJoinColumns = @JoinColumn(name = "id_profesional")
    )
    @Builder.Default
    private Set<Profesional> profesionales = new HashSet<>();

    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }
}
