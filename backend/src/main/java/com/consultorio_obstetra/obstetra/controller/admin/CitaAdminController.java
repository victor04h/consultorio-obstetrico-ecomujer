package com.consultorio_obstetra.obstetra.controller.admin;

import com.consultorio_obstetra.obstetra.dto.admin.cita.*;
import com.consultorio_obstetra.obstetra.service.admin.CitaAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/citas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('PERM_CITAS_ACCESO')")
public class CitaAdminController {

    private final CitaAdminService citaAdminService;

    @GetMapping
    public ResponseEntity<List<CitaResponse>> listar(@RequestParam(required = false) Long idPaciente) {
        if (idPaciente != null) {
            return ResponseEntity.ok(citaAdminService.listarPorPaciente(idPaciente));
        }
        return ResponseEntity.ok(citaAdminService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(citaAdminService.obtener(id));
    }

    /** Historial de "Reprogramaciones": todas las citas reagendadas/rechazadas, para la seccion propia del panel. */
    @GetMapping("/reasignaciones")
    public ResponseEntity<List<ReasignacionResponse>> listarTodasReasignaciones() {
        return ResponseEntity.ok(citaAdminService.listarTodasReasignaciones());
    }

    @PostMapping
    public ResponseEntity<CitaResponse> crear(@Valid @RequestBody CitaRequest request) {
        return ResponseEntity.ok(citaAdminService.crear(request));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<CitaResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(citaAdminService.cancelar(id));
    }

    @GetMapping("/{id}/pagos")
    public ResponseEntity<List<PagoResponse>> listarPagos(@PathVariable Long id) {
        return ResponseEntity.ok(citaAdminService.listarPagos(id));
    }

    @PostMapping("/{id}/pagos")
    public ResponseEntity<PagoResponse> registrarPago(@PathVariable Long id,
                                                       @Valid @RequestBody RegistrarPagoRequest request) {
        return ResponseEntity.ok(citaAdminService.registrarPago(id, request));
    }

    @GetMapping("/{id}/reasignaciones")
    public ResponseEntity<List<ReasignacionResponse>> listarReasignaciones(@PathVariable Long id) {
        return ResponseEntity.ok(citaAdminService.listarReasignaciones(id));
    }

    @PostMapping("/{id}/reagendar")
    public ResponseEntity<ReasignacionResponse> reagendar(@PathVariable Long id,
                                                           @Valid @RequestBody ReagendarRequest request) {
        return ResponseEntity.ok(citaAdminService.reagendar(id, request));
    }
}
