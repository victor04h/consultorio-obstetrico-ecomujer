package com.consultorio_obstetra.obstetra.controller.admin;

import com.consultorio_obstetra.obstetra.dto.admin.perfil.PerfilRequest;
import com.consultorio_obstetra.obstetra.dto.admin.perfil.PerfilResponse;
import com.consultorio_obstetra.obstetra.dto.common.CambiarEstadoRequest;
import com.consultorio_obstetra.obstetra.service.admin.PerfilAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/perfiles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('PERM_PERFILES_ACCESO')")
public class PerfilAdminController {

    private final PerfilAdminService perfilAdminService;

    @GetMapping
    public ResponseEntity<List<PerfilResponse>> listar() {
        return ResponseEntity.ok(perfilAdminService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(perfilAdminService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<PerfilResponse> crear(@Valid @RequestBody PerfilRequest request) {
        return ResponseEntity.ok(perfilAdminService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerfilResponse> actualizar(@PathVariable Long id, @Valid @RequestBody PerfilRequest request) {
        return ResponseEntity.ok(perfilAdminService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PerfilResponse> cambiarEstado(@PathVariable Long id,
                                                         @Valid @RequestBody CambiarEstadoRequest request) {
        return ResponseEntity.ok(perfilAdminService.cambiarEstado(id, request.getEstado()));
    }
}
