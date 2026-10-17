package com.consultorio_obstetra.obstetra.controller.admin;

import com.consultorio_obstetra.obstetra.dto.admin.producto.ProductoRequest;
import com.consultorio_obstetra.obstetra.dto.admin.producto.ProductoResponse;
import com.consultorio_obstetra.obstetra.dto.common.CambiarEstadoRequest;
import com.consultorio_obstetra.obstetra.service.admin.ProductoAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/productos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('PERM_PRODUCTOS_ACCESO')")
public class ProductoAdminController {

    private final ProductoAdminService productoAdminService;

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {
        return ResponseEntity.ok(productoAdminService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(productoAdminService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoAdminService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoAdminService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ProductoResponse> cambiarEstado(@PathVariable Long id,
                                                           @Valid @RequestBody CambiarEstadoRequest request) {
        return ResponseEntity.ok(productoAdminService.cambiarEstado(id, request.getEstado()));
    }
}
