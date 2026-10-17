package com.consultorio_obstetra.obstetra.model;

import com.consultorio_obstetra.obstetra.model.enums.EstadoCita;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cita")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_profesional", nullable = false)
    private Profesional profesional;

    /**
     * Estas dos columnas (id_obstetra, id_sede) las exige la tabla `cita` de
     * la base de datos oficial (son NOT NULL, con llave foranea a `obstetra`
     * y `sede`). El sistema actual trabaja con Profesional/una unica sede;
     * ambas se completan solas al crear la cita (ver CitaAdminService.crear):
     * obstetra apunta al espejo sincronizado de este mismo Profesional
     * (Obstetra.java), y sede a la "Sede Principal" sembrada por defecto.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_obstetra", nullable = false)
    private Obstetra obstetra;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sede", nullable = false)
    private Sede sede;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /**
     * `fecha`/`hora` (columnas separadas, NOT NULL en la base oficial) se
     * derivan solas de fechaHora al guardar (ver sincronizarFechaHora); el
     * sistema solo trabaja con fechaHora en el resto del codigo.
     */
    @Column
    private java.time.LocalDate fecha;

    @Column
    private java.time.LocalTime hora;

    @Column(name = "motivo_cita", length = 300)
    private String motivoCita;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoCita estado = EstadoCita.AGENDADA;

    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private LocalDateTime fechaModificacion;

    @PrePersist
    public void prePersist() {
        sincronizarFechaHora();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now();
        sincronizarFechaHora();
    }

    private void sincronizarFechaHora() {
        if (fechaHora != null) {
            this.fecha = fechaHora.toLocalDate();
            this.hora = fechaHora.toLocalTime();
        }
    }
}
