package com.consultorio_obstetra.obstetra.controller.admin;

import com.consultorio_obstetra.obstetra.dto.admin.calificacion.CalificacionResponse;
import com.consultorio_obstetra.obstetra.repository.CalificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Vista de solo lectura de las calificaciones que los pacientes dejan a los
 * profesionales (las genera el paciente desde el sitio publico; el
 * dashboard solo las supervisa).
 */
@RestController
@RequestMapping("/api/admin/calificaciones")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('PERM_CALIFICACIONES_ACCESO')")
public class CalificacionAdminController {

    private final CalificacionRepository calificacionRepository;

    @GetMapping
    public ResponseEntity<List<CalificacionResponse>> listar(@RequestParam(required = false) Long idProfesional) {
        var calificaciones = idProfesional != null
                ? calificacionRepository.findByProfesional_Id(idProfesional)
                : calificacionRepository.findAll();

        List<CalificacionResponse> respuesta = calificaciones.stream()
                .map(c -> CalificacionResponse.builder()
                        .id(c.getId())
                        .idProfesional(c.getProfesional().getId())
                        .nombreProfesional(c.getProfesional().getNombres() + " " + c.getProfesional().getApellidos())
                        .idPaciente(c.getPaciente().getId())
                        .nombrePaciente(c.getPaciente().getPersona().getNombres() + " "
                                + c.getPaciente().getPersona().getApellidos())
                        .puntuacion(c.getPuntuacion())
                        .comentario(c.getComentario())
                        .fechaCreacion(c.getFechaCreacion())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }
}
