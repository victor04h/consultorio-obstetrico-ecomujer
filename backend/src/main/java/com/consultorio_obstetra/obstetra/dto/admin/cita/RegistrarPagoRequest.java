package com.consultorio_obstetra.obstetra.dto.admin.cita;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RegistrarPagoRequest {

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", message = "El monto no puede ser negativo")
    private BigDecimal monto;

    private String metodoPago;

    private String referenciaPasarela;

    @NotNull(message = "Debes indicar si el pago se aprueba o se rechaza")
    private Boolean aprobado;
}
