package com.consultorio_obstetra.obstetra.controller.admin;

import com.consultorio_obstetra.obstetra.dto.admin.paciente.PacienteResponse;
import com.consultorio_obstetra.obstetra.exception.ApiException;
import com.consultorio_obstetra.obstetra.model.Paciente;
import com.consultorio_obstetra.obstetra.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Vista de solo lectura de los pacientes registrados (el alta la hace el
 * propio paciente via el proceso de Registro), util para que el
 * administrador los busque desde Citas / Historial Clinico.
 */
@RestController
@RequestMapping("/api/admin/pacientes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('PERM_PACIENTES_ACCESO')")
public class PacienteAdminController {

    private final PacienteRepository pacienteRepository;

    @GetMapping
    public ResponseEntity<List<PacienteResponse>> listar(@RequestParam(required = false) String busqueda) {
        List<Paciente> pacientes = (busqueda == null || busqueda.isBlank())
                ? pacienteRepository.findAll()
                : pacienteRepository.findByPersona_NombresContainingIgnoreCaseOrPersona_ApellidosContainingIgnoreCase(
                        busqueda, busqueda);
        return ResponseEntity.ok(pacientes.stream().map(this::aResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponse> obtener(@PathVariable Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> ApiException.noEncontrado("Paciente no encontrado"));
        return ResponseEntity.ok(aResponse(paciente));
    }

    private PacienteResponse aResponse(Paciente paciente) {
        return PacienteResponse.builder()
                .id(paciente.getId())
                .numeroDocumento(paciente.getPersona().getNumeroDocumento())
                .nombres(paciente.getPersona().getNombres())
                .apellidos(paciente.getPersona().getApellidos())
                .fechaNacimiento(paciente.getPersona().getFechaNacimiento())
                .telefono(paciente.getPersona().getTelefono())
                .correo(paciente.getUsuario().getCorreo())
                .esMenorEdad(paciente.isEsMenorEdad())
                .nombreApoderado(paciente.getApoderado() != null
                        ? paciente.getApoderado().getNombres() + " " + paciente.getApoderado().getApellidos()
                        : null)
                .build();
    }
}
