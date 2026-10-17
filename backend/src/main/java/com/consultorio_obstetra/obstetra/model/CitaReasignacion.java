package com.consultorio_obstetra.obstetra.model;

import com.consultorio_obstetra.obstetra.model.enums.ResultadoReasignacion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cita_reasignacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaReasignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cita", nullable = false)
    private Cita cita;

    @Column(name = "fecha_hora_anterior", nullable = false)
    private LocalDateTime fechaHoraAnterior;

    @Column(name = "fecha_hora_nueva")
    private LocalDateTime fechaHoraNueva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultadoReasignacion resultado;

    @Column(name = "motivo_rechazo", length = 255)
    private String motivoRechazo;

    @Builder.Default
    private LocalDateTime fechaSolicitud = LocalDateTime.now();
}
