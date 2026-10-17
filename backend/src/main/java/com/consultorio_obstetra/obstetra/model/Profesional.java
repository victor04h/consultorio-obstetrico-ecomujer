package com.consultorio_obstetra.obstetra.model;

import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Obstetra / medico. Se activa o desactiva (nunca se borra) desde el
 * "Modulo de Medicos" del BPM.
 */
@Entity
@Table(name = "profesional")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profesional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(length = 150)
    private String especialidad;

    // El nombre de columna se deja en ASCII (anios_experiencia) por
    // compatibilidad con la base de datos; solo el nombre en Java lleva la ñ.
    @Column(name = "anios_experiencia")
    private Integer añosExperiencia;

    // Idem: columna fisica "resena" (ASCII), propiedad en Java "reseña".
    @Column(name = "resena", columnDefinition = "TEXT")
    private String reseña;

    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoActivo estado = EstadoActivo.ACTIVO;

    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private LocalDateTime fechaModificacion;

    @ManyToMany(mappedBy = "profesionales", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Producto> productos = new HashSet<>();

    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }
}
