package com.consultorio_obstetra.obstetra.service.admin;

import com.consultorio_obstetra.obstetra.dto.admin.cita.*;
import com.consultorio_obstetra.obstetra.exception.ApiException;
import com.consultorio_obstetra.obstetra.model.*;
import com.consultorio_obstetra.obstetra.model.enums.AccionAuditoria;
import com.consultorio_obstetra.obstetra.model.enums.EstadoCita;
import com.consultorio_obstetra.obstetra.model.enums.EstadoPago;
import com.consultorio_obstetra.obstetra.model.enums.ResultadoReasignacion;
import com.consultorio_obstetra.obstetra.repository.*;
import com.consultorio_obstetra.obstetra.security.SecurityUtils;
import com.consultorio_obstetra.obstetra.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementa "Agendar Cita" y "Reagendar Cita" del BPM desde el punto de
 * vista del dashboard administrativo: crear/consultar citas, confirmar o
 * rechazar el pago, y aprobar o rechazar solicitudes de reasignacion.
 */
@Service
@RequiredArgsConstructor
public class CitaAdminService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final ProductoRepository productoRepository;
    private final ProfesionalRepository profesionalRepository;
    private final ObstetraRepository obstetraRepository;
    private final SedeRepository sedeRepository;
    private final PagoRepository pagoRepository;
    private final CitaReasignacionRepository citaReasignacionRepository;
    private final AuditoriaService auditoriaService;

    public List<CitaResponse> listar() {
        return filtrarSiEsObstetra(citaRepository.findAll()).stream()
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    public List<CitaResponse> listarPorPaciente(Long idPaciente) {
        return filtrarSiEsObstetra(citaRepository.findByPaciente_Id(idPaciente)).stream()
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    /**
     * Si quien consulta tiene perfil "Obstetra", solo devuelve las citas
     * asignadas a su propio Profesional vinculado; cualquier otro perfil
     * (Administrador, Secretaria) ve la lista completa sin filtrar.
     */
    private List<Cita> filtrarSiEsObstetra(List<Cita> citas) {
        return SecurityUtils.usuarioActual()
                .filter(u -> "Obstetra".equalsIgnoreCase(u.getPerfil().getNombre()))
                .map(u -> {
                    Long idProfesionalPropio = u.getProfesional() != null
                            ? u.getProfesional().getId()
                            : null;

                    return citas.stream()
                            .filter(c -> idProfesionalPropio != null
                                    && idProfesionalPropio.equals(
                                            c.getProfesional().getId()))
                            .collect(Collectors.toList());
                })
                .orElse(citas);
    }

    public CitaResponse obtener(Long id) {
        return aResponse(buscar(id));
    }

    @Transactional
    public CitaResponse crear(CitaRequest request) {

        Paciente paciente = pacienteRepository.findById(request.getIdPaciente())
                .orElseThrow(() ->
                        ApiException.solicitudInvalida(
                                "El paciente indicado no existe"));

        Producto producto = productoRepository.findById(request.getIdProducto())
                .orElseThrow(() ->
                        ApiException.solicitudInvalida(
                                "El producto/servicio indicado no existe"));

        Profesional profesional = profesionalRepository
                .findById(request.getIdProfesional())
                .orElseThrow(() ->
                        ApiException.solicitudInvalida(
                                "El profesional indicado no existe"));

        // id_obstetra / id_sede: exigidos por la base de datos oficial.
        // El obstetra es el espejo sincronizado de este mismo Profesional;
        // la sede es la unica sede por defecto.
        Obstetra obstetra = obstetraRepository.findById(profesional.getId())
                .orElseThrow(() ->
                        ApiException.solicitudInvalida(
                                "El profesional indicado todavia no esta sincronizado como obstetra"));

        Sede sede = sedeRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() ->
                        ApiException.solicitudInvalida(
                                "No hay ninguna sede configurada"));

        Cita cita = citaRepository.save(
                Cita.builder()
                        .paciente(paciente)
                        .producto(producto)
                        .profesional(profesional)
                        .obstetra(obstetra)
                        .sede(sede)
                        .fechaHora(request.getFechaHora())
                        .motivoCita(request.getMotivoCita())
                        .estado(EstadoCita.AGENDADA)
                        .build()
        );

        auditoriaService.registrar(
                "Cita",
                cita.getId(),
                AccionAuditoria.CREACION,
                "Cita agendada para "
                        + paciente.getPersona().getNombres()
                        + " "
                        + paciente.getPersona().getApellidos()
        );

        return aResponse(cita);
    }

    @Transactional
    public CitaResponse cancelar(Long id) {

        Cita cita = buscar(id);

        cita.setEstado(EstadoCita.CANCELADA);

        cita = citaRepository.save(cita);

        auditoriaService.registrar(
                "Cita",
                cita.getId(),
                AccionAuditoria.EDICION,
                "Cita cancelada"
        );

        return aResponse(cita);
    }

    // ---- Pago (rama de "Notificar pago rechazado" / confirmar pago) ----

    public List<PagoResponse> listarPagos(Long idCita) {

        buscar(idCita);

        return pagoRepository.findByCita_Id(idCita)
                .stream()
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PagoResponse registrarPago(
            Long idCita,
            RegistrarPagoRequest request) {

        Cita cita = buscar(idCita);

        Pago pago = Pago.builder()
                .cita(cita)
                .monto(request.getMonto())
                .metodoPago(request.getMetodoPago())
                .referenciaPasarela(request.getReferenciaPasarela())
                .estado(
                        request.getAprobado()
                                ? EstadoPago.APROBADO
                                : EstadoPago.RECHAZADO
                )
                .fechaPago(LocalDateTime.now())
                .build();

        pago = pagoRepository.save(pago);

        cita.setEstado(
                request.getAprobado()
                        ? EstadoCita.PAGADA
                        : EstadoCita.RECHAZADA
        );

        citaRepository.save(cita);

        auditoriaService.registrar(
                "Pago",
                pago.getId(),
                AccionAuditoria.CREACION,
                "Pago "
                        + pago.getEstado()
                        + " para la cita #"
                        + idCita
        );

        return aResponse(pago);
    }

    // ---- Reagendar Cita ----

    public List<ReasignacionResponse> listarReasignaciones(Long idCita) {

        buscar(idCita);

        return citaReasignacionRepository.findByCita_Id(idCita)
                .stream()
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    /**
     * Historial de "Reprogramaciones" (reagendamientos) de todas las citas,
     * para la seccion propia del dashboard. Si quien consulta es Obstetra,
     * solo ve las reprogramaciones de sus propias citas.
     */
    public List<ReasignacionResponse> listarTodasReasignaciones() {

        List<CitaReasignacion> reasignaciones =
                citaReasignacionRepository
                        .findAllByOrderByFechaSolicitudDesc();

        /*
         * Se crea una referencia final porque la variable
         * "reasignaciones" se reasigna posteriormente.
         */
        final List<CitaReasignacion> reasignacionesOriginales = reasignaciones;

        reasignaciones = SecurityUtils.usuarioActual()
                .filter(u -> "Obstetra".equalsIgnoreCase(
                        u.getPerfil().getNombre()))
                .map(u -> {

                    Long idProfesionalPropio =
                            u.getProfesional() != null
                                    ? u.getProfesional().getId()
                                    : null;

                    return reasignacionesOriginales.stream()
                            .filter(r ->
                                    idProfesionalPropio != null
                                            && idProfesionalPropio.equals(
                                                    r.getCita()
                                                            .getProfesional()
                                                            .getId())
                            )
                            .collect(Collectors.toList());
                })
                .orElse(reasignacionesOriginales);

        return reasignaciones.stream()
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReasignacionResponse reagendar(
            Long idCita,
            ReagendarRequest request) {

        Cita cita = buscar(idCita);

        boolean aprobar = Boolean.TRUE.equals(request.getAprobar());

        CitaReasignacion reasignacion = CitaReasignacion.builder()
                .cita(cita)
                .fechaHoraAnterior(cita.getFechaHora())
                .fechaHoraNueva(
                        aprobar
                                ? request.getFechaHoraNueva()
                                : null
                )
                .resultado(
                        aprobar
                                ? ResultadoReasignacion.APROBADA
                                : ResultadoReasignacion.RECHAZADA
                )
                .motivoRechazo(
                        aprobar
                                ? null
                                : request.getMotivoRechazo()
                )
                .build();

        reasignacion = citaReasignacionRepository.save(reasignacion);

        if (aprobar) {

            cita.setFechaHora(request.getFechaHoraNueva());
            cita.setEstado(EstadoCita.REASIGNADA);

            citaRepository.save(cita);
        }

        auditoriaService.registrar(
                "CitaReasignacion",
                reasignacion.getId(),
                aprobar
                        ? AccionAuditoria.EDICION
                        : AccionAuditoria.EDICION,
                "Reasignación de cita #"
                        + idCita
                        + " "
                        + reasignacion.getResultado()
        );

        return aResponse(reasignacion);
    }

    private Cita buscar(Long id) {

        return citaRepository.findById(id)
                .orElseThrow(() ->
                        ApiException.noEncontrado(
                                "Cita no encontrada"));
    }

    private CitaResponse aResponse(Cita cita) {

        return CitaResponse.builder()
                .id(cita.getId())
                .idPaciente(cita.getPaciente().getId())
                .nombrePaciente(
                        cita.getPaciente().getPersona().getNombres()
                                + " "
                                + cita.getPaciente().getPersona().getApellidos()
                )
                .idProducto(cita.getProducto().getId())
                .nombreProducto(cita.getProducto().getNombre())
                .idProfesional(cita.getProfesional().getId())
                .nombreProfesional(
                        cita.getProfesional().getNombres()
                                + " "
                                + cita.getProfesional().getApellidos()
                )
                .fechaHora(cita.getFechaHora())
                .motivoCita(cita.getMotivoCita())
                .estado(cita.getEstado())
                .build();
    }

    private PagoResponse aResponse(Pago pago) {

        return PagoResponse.builder()
                .id(pago.getId())
                .idCita(pago.getCita().getId())
                .monto(pago.getMonto())
                .estado(pago.getEstado())
                .metodoPago(pago.getMetodoPago())
                .referenciaPasarela(pago.getReferenciaPasarela())
                .fechaPago(pago.getFechaPago())
                .build();
    }

    private ReasignacionResponse aResponse(
            CitaReasignacion reasignacion) {

        Cita cita = reasignacion.getCita();

        return ReasignacionResponse.builder()
                .id(reasignacion.getId())
                .idCita(cita.getId())
                .nombrePaciente(
                        cita.getPaciente().getPersona().getNombres()
                                + " "
                                + cita.getPaciente().getPersona().getApellidos()
                )
                .nombreProfesional(
                        cita.getProfesional().getNombres()
                                + " "
                                + cita.getProfesional().getApellidos()
                )
                .nombreProducto(cita.getProducto().getNombre())
                .fechaHoraAnterior(
                        reasignacion.getFechaHoraAnterior())
                .fechaHoraNueva(
                        reasignacion.getFechaHoraNueva())
                .resultado(reasignacion.getResultado())
                .motivoRechazo(reasignacion.getMotivoRechazo())
                .fechaSolicitud(reasignacion.getFechaSolicitud())
                .build();
    }
}