package com.consultorio_obstetra.obstetra.service.admin;

import com.consultorio_obstetra.obstetra.dto.admin.historial.HistorialRequest;
import com.consultorio_obstetra.obstetra.dto.admin.historial.HistorialResponse;
import com.consultorio_obstetra.obstetra.exception.ApiException;
import com.consultorio_obstetra.obstetra.model.Cita;
import com.consultorio_obstetra.obstetra.model.HistorialClinico;
import com.consultorio_obstetra.obstetra.model.Obstetra;
import com.consultorio_obstetra.obstetra.model.Paciente;
import com.consultorio_obstetra.obstetra.model.Profesional;
import com.consultorio_obstetra.obstetra.model.enums.AccionAuditoria;
import com.consultorio_obstetra.obstetra.repository.CitaRepository;
import com.consultorio_obstetra.obstetra.repository.HistorialClinicoRepository;
import com.consultorio_obstetra.obstetra.repository.ObstetraRepository;
import com.consultorio_obstetra.obstetra.repository.PacienteRepository;
import com.consultorio_obstetra.obstetra.repository.ProfesionalRepository;
import com.consultorio_obstetra.obstetra.security.SecurityUtils;
import com.consultorio_obstetra.obstetra.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * "Historial Clínico" del BPM, rama de busqueda/actualizacion por parte del
 * administrador o profesional (la rama de autoconsulta del paciente
 * corresponde al futuro portal del paciente, no a este dashboard).
 */
@Service
@RequiredArgsConstructor
public class HistorialClinicoAdminService {

    private final HistorialClinicoRepository historialRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfesionalRepository profesionalRepository;
    private final ObstetraRepository obstetraRepository;
    private final CitaRepository citaRepository;
    private final AuditoriaService auditoriaService;

    public List<HistorialResponse> listarPorPaciente(Long idPaciente) {
        List<HistorialClinico> historial = historialRepository.findByPaciente_IdOrderByFechaRegistroDesc(idPaciente);
        Long idProfesionalPropio = idProfesionalDelObstetraActual();
        if (idProfesionalPropio != null) {
            historial = historial.stream()
                    .filter(h -> idProfesionalPropio.equals(h.getProfesional().getId()))
                    .collect(Collectors.toList());
        }
        return historial.stream().map(this::aResponse).collect(Collectors.toList());
    }

    /**
     * Si quien consulta tiene perfil "Obstetra", devuelve el id de su propio
     * Profesional vinculado (para filtrar/forzar sus entradas); para
     * cualquier otro perfil devuelve null (sin restriccion).
     */
    private Long idProfesionalDelObstetraActual() {
        return SecurityUtils.usuarioActual()
                .filter(u -> "Obstetra".equalsIgnoreCase(u.getPerfil().getNombre()))
                .map(u -> u.getProfesional() != null ? u.getProfesional().getId() : -1L)
                .orElse(null);
    }

    public HistorialResponse obtener(Long id) {
        return aResponse(buscar(id));
    }

    @Transactional
    public HistorialResponse crear(HistorialRequest request) {
        Paciente paciente = pacienteRepository.findById(request.getIdPaciente())
                .orElseThrow(() -> ApiException.solicitudInvalida("El paciente indicado no existe"));
        Long idProfesionalPropio = idProfesionalDelObstetraActual();
        Long idProfesionalAUsar = idProfesionalPropio != null ? idProfesionalPropio : request.getIdProfesional();
        Profesional profesional = profesionalRepository.findById(idProfesionalAUsar)
                .orElseThrow(() -> ApiException.solicitudInvalida("El profesional indicado no existe"));
        // id_obstetra: exigido por la base de datos oficial (ver comentario
        // en HistorialClinico.java); es el espejo sincronizado del mismo Profesional.
        Obstetra obstetra = obstetraRepository.findById(profesional.getId())
                .orElseThrow(() -> ApiException.solicitudInvalida(
                        "El profesional indicado todavia no esta sincronizado como obstetra"));
        Cita cita = null;
        if (request.getIdCita() != null) {
            cita = citaRepository.findById(request.getIdCita())
                    .orElseThrow(() -> ApiException.solicitudInvalida("La cita indicada no existe"));
        }

        HistorialClinico historial = historialRepository.save(HistorialClinico.builder()
                .paciente(paciente)
                .cita(cita)
                .profesional(profesional)
                .obstetra(obstetra)
                .diagnostico(request.getDiagnostico())
                .tratamiento(request.getTratamiento())
                .notas(request.getNotas())
                .build());

        auditoriaService.registrar("HistorialClinico", historial.getId(), AccionAuditoria.CREACION,
                "Entrada de historial clínico creada para el paciente #" + paciente.getId());
        return aResponse(historial);
    }

    @Transactional
    public HistorialResponse actualizar(Long id, HistorialRequest request) {
        HistorialClinico historial = buscar(id);

        Long idProfesionalPropio = idProfesionalDelObstetraActual();
        if (idProfesionalPropio != null && !idProfesionalPropio.equals(historial.getProfesional().getId())) {
            throw ApiException.noAutorizado("No puedes editar el historial clínico de otro profesional");
        }
        Long idProfesionalAUsar = idProfesionalPropio != null ? idProfesionalPropio : request.getIdProfesional();
        Profesional profesional = profesionalRepository.findById(idProfesionalAUsar)
                .orElseThrow(() -> ApiException.solicitudInvalida("El profesional indicado no existe"));
        Obstetra obstetra = obstetraRepository.findById(profesional.getId())
                .orElseThrow(() -> ApiException.solicitudInvalida(
                        "El profesional indicado todavia no esta sincronizado como obstetra"));

        historial.setProfesional(profesional);
        historial.setObstetra(obstetra);
        historial.setDiagnostico(request.getDiagnostico());
        historial.setTratamiento(request.getTratamiento());
        historial.setNotas(request.getNotas());
        historial = historialRepository.save(historial);

        auditoriaService.registrar("HistorialClinico", historial.getId(), AccionAuditoria.EDICION,
                "Entrada de historial clínico editada");
        return aResponse(historial);
    }

    private HistorialClinico buscar(Long id) {
        return historialRepository.findById(id)
                .orElseThrow(() -> ApiException.noEncontrado("Entrada de historial clínico no encontrada"));
    }

    private HistorialResponse aResponse(HistorialClinico historial) {
        return HistorialResponse.builder()
                .id(historial.getId())
                .idPaciente(historial.getPaciente().getId())
                .nombrePaciente(historial.getPaciente().getPersona().getNombres() + " "
                        + historial.getPaciente().getPersona().getApellidos())
                .idCita(historial.getCita() != null ? historial.getCita().getId() : null)
                .idProfesional(historial.getProfesional().getId())
                .nombreProfesional(historial.getProfesional().getNombres() + " "
                        + historial.getProfesional().getApellidos())
                .diagnostico(historial.getDiagnostico())
                .tratamiento(historial.getTratamiento())
                .notas(historial.getNotas())
                .fechaRegistro(historial.getFechaRegistro())
                .build();
    }
}
