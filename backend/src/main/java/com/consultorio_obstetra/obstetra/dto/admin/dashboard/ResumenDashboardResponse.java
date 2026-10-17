package com.consultorio_obstetra.obstetra.dto.admin.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/** Datos para la pantalla de inicio del dashboard administrativo. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenDashboardResponse {
    private long totalPacientes;
    private long totalProfesionalesActivos;
    private long totalCitasHoy;
    private long totalCitasPendientesPago;
    private BigDecimal ingresosMesActual;
    private Map<String, Long> citasPorEstado;
}
