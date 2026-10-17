package com.consultorio_obstetra.obstetra.controller.admin;

import com.consultorio_obstetra.obstetra.dto.admin.perfil.PermisoRequest;
import com.consultorio_obstetra.obstetra.dto.admin.perfil.PermisoResponse;
import com.consultorio_obstetra.obstetra.dto.common.MensajeResponse;
import com.consultorio_obstetra.obstetra.service.admin.PermisoAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/permisos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('PERM_PERFILES_ACCESO')")
public class PermisoAdminController {

    private final PermisoAdminService permisoAdminService;

    @GetMapping
    public ResponseEntity<List<PermisoResponse>> listar() {
        return ResponseEntity.ok(permisoAdminService.listar());
    }

    @PostMapping
    public ResponseEntity<PermisoResponse> crear(@Valid @RequestBody PermisoRequest request) {
        return ResponseEntity.ok(permisoAdminService.crear(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Long id) {
        permisoAdminService.eliminar(id);
        return ResponseEntity.ok(MensajeResponse.de("Permiso eliminado correctamente"));
    }
}
