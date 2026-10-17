package com.consultorio_obstetra.obstetra.controller.admin;

import com.consultorio_obstetra.obstetra.dto.admin.historial.HistorialRequest;
import com.consultorio_obstetra.obstetra.dto.admin.historial.HistorialResponse;
import com.consultorio_obstetra.obstetra.service.admin.HistorialClinicoAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/historial-clinico")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('PERM_HISTORIAL_CLINICO_ACCESO')")
public class HistorialClinicoAdminController {

    private final HistorialClinicoAdminService historialAdminService;

    @GetMapping
    public ResponseEntity<List<HistorialResponse>> listarPorPaciente(@RequestParam Long idPaciente) {
        return ResponseEntity.ok(historialAdminService.listarPorPaciente(idPaciente));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistorialResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(historialAdminService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<HistorialResponse> crear(@Valid @RequestBody HistorialRequest request) {
        return ResponseEntity.ok(historialAdminService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistorialResponse> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody HistorialRequest request) {
        return ResponseEntity.ok(historialAdminService.actualizar(id, request));
    }
}
