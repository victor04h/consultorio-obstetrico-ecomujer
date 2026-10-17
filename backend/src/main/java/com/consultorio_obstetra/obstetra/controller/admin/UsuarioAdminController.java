package com.consultorio_obstetra.obstetra.controller.admin;

import com.consultorio_obstetra.obstetra.dto.admin.usuario.UsuarioInternoRequest;
import com.consultorio_obstetra.obstetra.dto.admin.usuario.UsuarioInternoResponse;
import com.consultorio_obstetra.obstetra.dto.common.CambiarEstadoRequest;
import com.consultorio_obstetra.obstetra.service.admin.UsuarioAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Modulo "Usuarios" (cuentas internas): solo el Administrador puede crear o
 * desactivar cuentas de acceso al panel para Secretaria/Obstetra/otro
 * Administrador. A proposito NO se abre por checkbox de permisos como el
 * resto de modulos: delegar la creacion de cuentas es un riesgo de
 * seguridad que no deberia activarse por accidente desde Perfiles.
 */
@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioAdminController {

    private final UsuarioAdminService usuarioAdminService;

    @GetMapping
    public ResponseEntity<List<UsuarioInternoResponse>> listar() {
        return ResponseEntity.ok(usuarioAdminService.listar());
    }

    @PostMapping
    public ResponseEntity<UsuarioInternoResponse> crear(@Valid @RequestBody UsuarioInternoRequest request) {
        return ResponseEntity.ok(usuarioAdminService.crear(request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioInternoResponse> cambiarEstado(@PathVariable Long id,
                                                                 @Valid @RequestBody CambiarEstadoRequest request) {
        return ResponseEntity.ok(usuarioAdminService.cambiarEstado(id, request.getEstado()));
    }
}
