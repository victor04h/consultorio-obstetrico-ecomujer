package com.consultorio_obstetra.obstetra.model;

import com.consultorio_obstetra.obstetra.model.enums.EstadoPago;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cita", nullable = false)
    private Cita cita;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "referencia_pasarela", length = 150)
    private String referenciaPasarela;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
