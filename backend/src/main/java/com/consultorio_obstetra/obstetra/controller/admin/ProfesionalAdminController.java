package com.consultorio_obstetra.obstetra.controller.admin;

import com.consultorio_obstetra.obstetra.dto.admin.profesional.DisponibilidadRequest;
import com.consultorio_obstetra.obstetra.dto.admin.profesional.DisponibilidadResponse;
import com.consultorio_obstetra.obstetra.dto.admin.profesional.ProfesionalRequest;
import com.consultorio_obstetra.obstetra.dto.admin.profesional.ProfesionalResponse;
import com.consultorio_obstetra.obstetra.dto.common.CambiarEstadoRequest;
import com.consultorio_obstetra.obstetra.dto.common.MensajeResponse;
import com.consultorio_obstetra.obstetra.service.admin.ProfesionalAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/profesionales")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('PERM_PROFESIONALES_ACCESO')")
public class ProfesionalAdminController {

    private final ProfesionalAdminService profesionalAdminService;

    @GetMapping
    public ResponseEntity<List<ProfesionalResponse>> listar() {
        return ResponseEntity.ok(profesionalAdminService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfesionalResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(profesionalAdminService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<ProfesionalResponse> crear(@Valid @RequestBody ProfesionalRequest request) {
        return ResponseEntity.ok(profesionalAdminService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfesionalResponse> actualizar(@PathVariable Long id,
                                                           @Valid @RequestBody ProfesionalRequest request) {
        return ResponseEntity.ok(profesionalAdminService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ProfesionalResponse> cambiarEstado(@PathVariable Long id,
                                                              @Valid @RequestBody CambiarEstadoRequest request) {
        return ResponseEntity.ok(profesionalAdminService.cambiarEstado(id, request.getEstado()));
    }

    @GetMapping("/{id}/horarios")
    public ResponseEntity<List<DisponibilidadResponse>> listarHorarios(@PathVariable Long id) {
        return ResponseEntity.ok(profesionalAdminService.listarHorarios(id));
    }

    @PostMapping("/{id}/horarios")
    public ResponseEntity<DisponibilidadResponse> agregarHorario(@PathVariable Long id,
                                                                  @Valid @RequestBody DisponibilidadRequest request) {
        return ResponseEntity.ok(profesionalAdminService.agregarHorario(id, request));
    }

    @DeleteMapping("/{id}/horarios/{idHorario}")
    public ResponseEntity<MensajeResponse> eliminarHorario(@PathVariable Long id, @PathVariable Long idHorario) {
        profesionalAdminService.eliminarHorario(id, idHorario);
        return ResponseEntity.ok(MensajeResponse.de("Horario eliminado correctamente"));
    }
}
