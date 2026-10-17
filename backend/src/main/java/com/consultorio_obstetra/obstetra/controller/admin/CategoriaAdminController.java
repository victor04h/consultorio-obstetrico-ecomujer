package com.consultorio_obstetra.obstetra.controller.admin;

import com.consultorio_obstetra.obstetra.dto.admin.categoria.CategoriaRequest;
import com.consultorio_obstetra.obstetra.dto.admin.categoria.CategoriaResponse;
import com.consultorio_obstetra.obstetra.dto.common.CambiarEstadoRequest;
import com.consultorio_obstetra.obstetra.service.admin.CategoriaAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categorias")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('PERM_CATEGORIAS_ACCESO')")
public class CategoriaAdminController {

    private final CategoriaAdminService categoriaAdminService;

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {
        return ResponseEntity.ok(categoriaAdminService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaAdminService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> crear(@Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaAdminService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaAdminService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CategoriaResponse> cambiarEstado(@PathVariable Long id,
                                                            @Valid @RequestBody CambiarEstadoRequest request) {
        return ResponseEntity.ok(categoriaAdminService.cambiarEstado(id, request.getEstado()));
    }
}
