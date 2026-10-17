package com.consultorio_obstetra.obstetra.model;

import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Tabla `obstetra` de la base de datos oficial del curso. El sistema ya
 * maneja al medico como "Profesional" (con su propio formulario, foto,
 * anios de experiencia, resena, etc. — ver Profesional.java); esta entidad
 * es un espejo de solo-sincronizacion: cada vez que se crea/edita un
 * Profesional, se refleja aqui automaticamente CON EL MISMO ID (ver
 * ProfesionalAdminService.sincronizarObstetra), unicamente para que las
 * columnas `cita.id_obstetra` e `historial_clinico.id_obstetra` (NOT NULL,
 * FK a esta tabla) tengan un registro valido al que apuntar. No se edita
 * directamente desde ningun formulario propio.
 */
@Entity
@Table(name = "obstetra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Obstetra {

    @Id
    private Long id;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(name = "anios_experiencia")
    private Integer aniosExperiencia;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoActivo estado = EstadoActivo.ACTIVO;

    @Column(name = "fecha_registro")
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}
