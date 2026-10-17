package com.consultorio_obstetra.obstetra.service.admin;

import com.consultorio_obstetra.obstetra.dto.admin.dashboard.ResumenDashboardResponse;
import com.consultorio_obstetra.obstetra.model.Cita;
import com.consultorio_obstetra.obstetra.model.Pago;
import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import com.consultorio_obstetra.obstetra.model.enums.EstadoCita;
import com.consultorio_obstetra.obstetra.model.enums.EstadoPago;
import com.consultorio_obstetra.obstetra.repository.CitaRepository;
import com.consultorio_obstetra.obstetra.repository.PacienteRepository;
import com.consultorio_obstetra.obstetra.repository.PagoRepository;
import com.consultorio_obstetra.obstetra.repository.ProfesionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardAdminService {

    private final PacienteRepository pacienteRepository;
    private final ProfesionalRepository profesionalRepository;
    private final CitaRepository citaRepository;
    private final PagoRepository pagoRepository;

    public ResumenDashboardResponse resumen() {
        List<Cita> citas = citaRepository.findAll();
        LocalDate hoy = LocalDate.now();

        long citasHoy = citas.stream()
                .filter(c -> c.getFechaHora() != null && c.getFechaHora().toLocalDate().isEqual(hoy))
                .count();

        long citasPendientesPago = citas.stream()
                .filter(c -> c.getEstado() == EstadoCita.AGENDADA)
                .count();

        Map<String, Long> citasPorEstado = citas.stream()
                .collect(Collectors.groupingBy(c -> c.getEstado().name(), Collectors.counting()));

        LocalDateTime inicioMes = hoy.withDayOfMonth(1).atStartOfDay();
        BigDecimal ingresosMes = pagoRepository.findAll().stream()
                .filter(p -> p.getEstado() == EstadoPago.APROBADO)
                .filter(p -> p.getFechaPago() != null && !p.getFechaPago().isBefore(inicioMes))
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResumenDashboardResponse.builder()
                .totalPacientes(pacienteRepository.count())
                .totalProfesionalesActivos(profesionalRepository.findByEstado(EstadoActivo.ACTIVO).size())
                .totalCitasHoy(citasHoy)
                .totalCitasPendientesPago(citasPendientesPago)
                .ingresosMesActual(ingresosMes)
                .citasPorEstado(citasPorEstado)
                .build();
    }
}
