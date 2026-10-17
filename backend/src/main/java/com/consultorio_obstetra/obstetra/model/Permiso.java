package com.consultorio_obstetra.obstetra.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "permiso",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"modulo", "accion"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Exigido por la tabla `permiso` de la base de datos.
     * Se autogenera a partir de modulo + accion.
     */
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String modulo;

    @Column(nullable = false, length = 50)
    private String accion;

    @Column(length = 150)
    private String descripcion;
}