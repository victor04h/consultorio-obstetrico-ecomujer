package com.consultorio_obstetra.obstetra.service.admin;

import com.consultorio_obstetra.obstetra.dto.admin.profesional.DisponibilidadRequest;
import com.consultorio_obstetra.obstetra.dto.admin.profesional.DisponibilidadResponse;
import com.consultorio_obstetra.obstetra.dto.admin.profesional.ProfesionalRequest;
import com.consultorio_obstetra.obstetra.dto.admin.profesional.ProfesionalResponse;
import com.consultorio_obstetra.obstetra.exception.ApiException;
import com.consultorio_obstetra.obstetra.model.Calificacion;
import com.consultorio_obstetra.obstetra.model.DisponibilidadHorario;
import com.consultorio_obstetra.obstetra.model.Obstetra;
import com.consultorio_obstetra.obstetra.model.Profesional;
import com.consultorio_obstetra.obstetra.model.enums.AccionAuditoria;
import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import com.consultorio_obstetra.obstetra.model.enums.EstadoDisponibilidad;
import com.consultorio_obstetra.obstetra.repository.CalificacionRepository;
import com.consultorio_obstetra.obstetra.repository.DisponibilidadHorarioRepository;
import com.consultorio_obstetra.obstetra.repository.ObstetraRepository;
import com.consultorio_obstetra.obstetra.repository.ProfesionalRepository;
import com.consultorio_obstetra.obstetra.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * "Modulo de Medicos" del BPM: alta/edicion de datos y especialidad,
 * gestion de horarios de disponibilidad, y activar/desactivar (nunca se
 * elimina de la base de datos).
 */
@Service
@RequiredArgsConstructor
public class ProfesionalAdminService {

    private final ProfesionalRepository profesionalRepository;
    private final DisponibilidadHorarioRepository disponibilidadRepository;
    private final CalificacionRepository calificacionRepository;
    private final ObstetraRepository obstetraRepository;
    private final AuditoriaService auditoriaService;

    public List<ProfesionalResponse> listar() {
        return profesionalRepository.findAll().stream().map(this::aResponse).collect(Collectors.toList());
    }

    public List<ProfesionalResponse> listarActivos() {
        return profesionalRepository.findByEstado(EstadoActivo.ACTIVO).stream()
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    public ProfesionalResponse obtener(Long id) {
        return aResponse(buscar(id));
    }

    @Transactional
    public ProfesionalResponse crear(ProfesionalRequest request) {
        Profesional profesional = Profesional.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .especialidad(request.getEspecialidad())
                .añosExperiencia(request.getAñosExperiencia())
                .reseña(request.getReseña())
                .fotoUrl(request.getFotoUrl())
                .estado(EstadoActivo.ACTIVO)
                .build();
        profesional = profesionalRepository.save(profesional);
        sincronizarObstetra(profesional);
        auditoriaService.registrar("Profesional", profesional.getId(), AccionAuditoria.CREACION,
                "Profesional creado: " + profesional.getNombres() + " " + profesional.getApellidos());
        return aResponse(profesional);
    }

    @Transactional
    public ProfesionalResponse actualizar(Long id, ProfesionalRequest request) {
        Profesional profesional = buscar(id);
        profesional.setNombres(request.getNombres());
        profesional.setApellidos(request.getApellidos());
        profesional.setEspecialidad(request.getEspecialidad());
        profesional.setAñosExperiencia(request.getAñosExperiencia());
        profesional.setReseña(request.getReseña());
        profesional.setFotoUrl(request.getFotoUrl());
        profesional = profesionalRepository.save(profesional);
        sincronizarObstetra(profesional);
        auditoriaService.registrar("Profesional", profesional.getId(), AccionAuditoria.EDICION,
                "Datos/especialidad editados: " + profesional.getNombres() + " " + profesional.getApellidos());
        return aResponse(profesional);
    }

    @Transactional
    public ProfesionalResponse cambiarEstado(Long id, EstadoActivo estado) {
        Profesional profesional = buscar(id);
        profesional.setEstado(estado);
        profesional = profesionalRepository.save(profesional);
        sincronizarObstetra(profesional);
        auditoriaService.registrar("Profesional", profesional.getId(), AccionAuditoria.INACTIVACION,
                "Profesional " + profesional.getNombres() + " " + profesional.getApellidos() + " -> " + estado);
        return aResponse(profesional);
    }

    /**
     * Refleja este Profesional en la tabla `obstetra` de la base de datos
     * oficial, con el MISMO id, para que `cita.id_obstetra` e
     * `historial_clinico.id_obstetra` (NOT NULL) siempre tengan a que
     * apuntar. Ver el comentario de la clase Obstetra.
     */
    private void sincronizarObstetra(Profesional profesional) {
        Obstetra obstetra = obstetraRepository.findById(profesional.getId())
                .orElseGet(() -> Obstetra.builder().id(profesional.getId()).build());
        obstetra.setNombreCompleto((profesional.getNombres() + " " + profesional.getApellidos()).trim());
        obstetra.setAniosExperiencia(profesional.getAñosExperiencia());
        obstetra.setDescripcion(profesional.getReseña());
        obstetra.setEstado(profesional.getEstado());
        obstetraRepository.save(obstetra);
    }

    // ---- Gestionar horarios ----

    public List<DisponibilidadResponse> listarHorarios(Long idProfesional) {
        buscar(idProfesional);
        return disponibilidadRepository.findByProfesional_Id(idProfesional).stream()
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DisponibilidadResponse agregarHorario(Long idProfesional, DisponibilidadRequest request) {
        Profesional profesional = buscar(idProfesional);
        if (!request.getHoraInicio().isBefore(request.getHoraFin())) {
            throw ApiException.solicitudInvalida("La hora de inicio debe ser anterior a la hora de fin");
        }
        DisponibilidadHorario horario = disponibilidadRepository.save(DisponibilidadHorario.builder()
                .profesional(profesional)
                .fecha(request.getFecha())
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .estado(EstadoDisponibilidad.LIBRE)
                .build());
        auditoriaService.registrar("DisponibilidadHorario", horario.getId(), AccionAuditoria.CREACION,
                "Horario agregado para " + profesional.getNombres() + " " + profesional.getApellidos()
                        + " el " + horario.getFecha());
        return aResponse(horario);
    }

    @Transactional
    public void eliminarHorario(Long idProfesional, Long idHorario) {
        DisponibilidadHorario horario = disponibilidadRepository.findById(idHorario)
                .orElseThrow(() -> ApiException.noEncontrado("Horario no encontrado"));
        if (!horario.getProfesional().getId().equals(idProfesional)) {
            throw ApiException.solicitudInvalida("El horario no pertenece a este profesional");
        }
        if (horario.getEstado() == EstadoDisponibilidad.OCUPADO) {
            throw ApiException.conflicto("No se puede eliminar un horario que ya tiene una cita asignada");
        }
        disponibilidadRepository.delete(horario);
        auditoriaService.registrar("DisponibilidadHorario", idHorario, AccionAuditoria.ELIMINACION,
                "Horario eliminado");
    }

    private Profesional buscar(Long id) {
        return profesionalRepository.findById(id)
                .orElseThrow(() -> ApiException.noEncontrado("Profesional no encontrado"));
    }

    private ProfesionalResponse aResponse(Profesional profesional) {
        List<Calificacion> calificaciones = calificacionRepository.findByProfesional_Id(profesional.getId());
        Double promedio = calificaciones.isEmpty() ? null
                : calificaciones.stream().mapToInt(Calificacion::getPuntuacion).average().orElse(0);

        return ProfesionalResponse.builder()
                .id(profesional.getId())
                .nombres(profesional.getNombres())
                .apellidos(profesional.getApellidos())
                .especialidad(profesional.getEspecialidad())
                .añosExperiencia(profesional.getAñosExperiencia())
                .reseña(profesional.getReseña())
                .fotoUrl(profesional.getFotoUrl())
                .estado(profesional.getEstado())
                .calificacionPromedio(promedio)
                .build();
    }

    private DisponibilidadResponse aResponse(DisponibilidadHorario horario) {
        return DisponibilidadResponse.builder()
                .id(horario.getId())
                .idProfesional(horario.getProfesional().getId())
                .fecha(horario.getFecha())
                .horaInicio(horario.getHoraInicio())
                .horaFin(horario.getHoraFin())
                .estado(horario.getEstado())
                .build();
    }
}
