package com.consultorio_obstetra.obstetra.controller.publico;

import com.consultorio_obstetra.obstetra.dto.admin.categoria.CategoriaResponse;
import com.consultorio_obstetra.obstetra.dto.admin.producto.ProductoResponse;
import com.consultorio_obstetra.obstetra.dto.admin.profesional.ProfesionalResponse;
import com.consultorio_obstetra.obstetra.service.admin.CategoriaAdminService;
import com.consultorio_obstetra.obstetra.service.admin.ProductoAdminService;
import com.consultorio_obstetra.obstetra.service.admin.ProfesionalAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Catalogo publico que ve el paciente antes de registrarse: "el cliente
 * va a la pagina, ve los especialistas, mira lo que ofrecemos" (proceso de
 * Registro del BPM). Sin autenticacion, solo lectura, solo activos.
 */
@RestController
@RequiredArgsConstructor
public class CatalogoPublicoController {

    private final CategoriaAdminService categoriaAdminService;
    private final ProductoAdminService productoAdminService;
    private final ProfesionalAdminService profesionalAdminService;

    @GetMapping("/api/categorias/publicas")
    public ResponseEntity<List<CategoriaResponse>> categorias() {
        return ResponseEntity.ok(categoriaAdminService.listarActivas());
    }

    @GetMapping("/api/productos/publicos")
    public ResponseEntity<List<ProductoResponse>> productos() {
        return ResponseEntity.ok(productoAdminService.listarDisponibles());
    }

    @GetMapping("/api/profesionales/publicos")
    public ResponseEntity<List<ProfesionalResponse>> profesionales() {
        return ResponseEntity.ok(profesionalAdminService.listarActivos());
    }
}
