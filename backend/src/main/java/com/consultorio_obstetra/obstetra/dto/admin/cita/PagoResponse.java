package com.consultorio_obstetra.obstetra.dto.admin.cita;

import com.consultorio_obstetra.obstetra.model.enums.EstadoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponse {
    private Long id;
    private Long idCita;
    private BigDecimal monto;
    private EstadoPago estado;
    private String metodoPago;
    private String referenciaPasarela;
    private LocalDateTime fechaPago;
}
