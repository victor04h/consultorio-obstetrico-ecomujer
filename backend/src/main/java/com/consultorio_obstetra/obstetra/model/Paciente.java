package com.consultorio_obstetra.obstetra.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Ficha de paciente. Su Persona (persona) es la del propio paciente.
 * Si es menor de edad, apoderado apunta a la Persona del padre/madre/apoderado
 * y usuario apunta a la cuenta de acceso (que pertenece al apoderado en ese caso).
 */
@Entity
@Table(name = "paciente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_persona", nullable = false, unique = true)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_apoderado_persona")
    private Persona apoderado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "es_menor_edad")
    @Builder.Default
    private boolean esMenorEdad = false;
}
