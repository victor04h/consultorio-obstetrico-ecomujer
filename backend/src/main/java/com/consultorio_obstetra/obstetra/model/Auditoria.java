package com.consultorio_obstetra.obstetra.model;

import com.consultorio_obstetra.obstetra.model.enums.AccionAuditoria;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(nullable = false, length = 50)
    private String entidad;

    @Column(name = "id_entidad", nullable = false)
    private Long idEntidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccionAuditoria accion;

    @Column(columnDefinition = "TEXT")
    private String detalle;

    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();
}
