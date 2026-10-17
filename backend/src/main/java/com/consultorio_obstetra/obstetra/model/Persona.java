package com.consultorio_obstetra.obstetra.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;

/**
 * Datos de identidad de una persona (paciente, apoderado o administrador),
 * tal como los devuelve la API externa de identidad a partir del numero
 * de documento (proceso de Registro del BPM).
 */
@Entity
@Table(name = "persona")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_documento", nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 30)
    private String telefono;

    @Transient
    public Integer getEdad() {
        if (fechaNacimiento == null) {
            return null;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    @Transient
    public boolean isMenorDeEdad() {
        Integer edad = getEdad();
        return edad != null && edad < 18;
    }
}
