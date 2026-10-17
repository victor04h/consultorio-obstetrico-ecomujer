package com.consultorio_obstetra.obstetra.model;

import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoActivo estado = EstadoActivo.ACTIVO;

    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
