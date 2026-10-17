package com.consultorio_obstetra.obstetra.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Local/sede del consultorio. La tabla `sede` ya existe en la base de datos
 * oficial del curso pero el sistema actual todavia no tiene un modulo propio
 * para administrar varias sedes (queda para una fase futura); mientras
 * tanto se usa una unica "Sede Principal" sembrada automaticamente (ver
 * DataSeeder), que es la que se asigna a todas las citas para satisfacer la
 * columna `cita.id_sede` (NOT NULL).
 */
@Entity
@Table(name = "sede")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sede")
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String direccion;

    @Column(length = 20)
    private String telefono;

    @Column(name = "horario_atencion", length = 150)
    private String horarioAtencion;
}
