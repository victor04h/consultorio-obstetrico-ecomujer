package com.consultorio_obstetra.obstetra.service.admin;

import com.consultorio_obstetra.obstetra.dto.admin.producto.ProductoRequest;
import com.consultorio_obstetra.obstetra.dto.admin.producto.ProductoResponse;
import com.consultorio_obstetra.obstetra.exception.ApiException;
import com.consultorio_obstetra.obstetra.model.Categoria;
import com.consultorio_obstetra.obstetra.model.Producto;
import com.consultorio_obstetra.obstetra.model.Profesional;
import com.consultorio_obstetra.obstetra.model.enums.AccionAuditoria;
import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import com.consultorio_obstetra.obstetra.model.enums.TipoProducto;
import com.consultorio_obstetra.obstetra.repository.CategoriaRepository;
import com.consultorio_obstetra.obstetra.repository.ProductoRepository;
import com.consultorio_obstetra.obstetra.repository.ProfesionalRepository;
import com.consultorio_obstetra.obstetra.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Modulo "Producto" del BPM (incluye el catalogo de ecografias/servicios
 * y medicamentos, agrupados por Categoria).
 */
@Service
@RequiredArgsConstructor
public class ProductoAdminService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProfesionalRepository profesionalRepository;
    private final AuditoriaService auditoriaService;

    public List<ProductoResponse> listar() {
        return productoRepository.findAll().stream().map(this::aResponse).collect(Collectors.toList());
    }

    public List<ProductoResponse> listarDisponibles() {
        return productoRepository.findByEstado(EstadoActivo.ACTIVO).stream()
                .filter(Producto::isDisponible)
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    public ProductoResponse obtener(Long id) {
        return aResponse(buscar(id));
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Categoria categoria = buscarCategoria(request.getIdCategoria());
        Producto producto = Producto.builder()
                .categoria(categoria)
                .nombre(request.getNombre())
                .descripcionCorta(request.getDescripcionCorta())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .disponible(request.isDisponible())
                .estado(EstadoActivo.ACTIVO)
                .tipo(inferirTipo(categoria))
                .profesionales(resolverProfesionales(request.getProfesionalIds()))
                .build();
        producto = productoRepository.save(producto);
        auditoriaService.registrar("Producto", producto.getId(), AccionAuditoria.CREACION,
                "Producto creado: " + producto.getNombre());
        return aResponse(producto);
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscar(id);
        Categoria categoria = buscarCategoria(request.getIdCategoria());
        producto.setCategoria(categoria);
        producto.setTipo(inferirTipo(categoria));
        producto.setNombre(request.getNombre());
        producto.setDescripcionCorta(request.getDescripcionCorta());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setDisponible(request.isDisponible());
        producto.setProfesionales(resolverProfesionales(request.getProfesionalIds()));
        producto = productoRepository.save(producto);
        auditoriaService.registrar("Producto", producto.getId(), AccionAuditoria.EDICION,
                "Producto editado: " + producto.getNombre());
        return aResponse(producto);
    }

    @Transactional
    public ProductoResponse cambiarEstado(Long id, EstadoActivo estado) {
        Producto producto = buscar(id);
        producto.setEstado(estado);
        producto = productoRepository.save(producto);
        auditoriaService.registrar("Producto", producto.getId(), AccionAuditoria.INACTIVACION,
                "Producto " + producto.getNombre() + " -> " + estado);
        return aResponse(producto);
    }

    /**
     * La base de datos oficial exige el campo `tipo` (medicamento/servicio de
     * ecografia) en cada producto, pero el formulario no lo pide: se infiere
     * del nombre de la Categoria, tal como indica el comentario de la clase
     * Producto ("Ecografias" vs "Medicamentos").
     */
    private TipoProducto inferirTipo(Categoria categoria) {
        String nombre = categoria.getNombre() == null ? "" : categoria.getNombre().toLowerCase();
        return nombre.contains("ecograf") ? TipoProducto.SERVICIO_ECOGRAFIA : TipoProducto.MEDICAMENTO;
    }

    private Set<Profesional> resolverProfesionales(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        Set<Profesional> profesionales = new HashSet<>(profesionalRepository.findAllById(ids));
        if (profesionales.size() != ids.size()) {
            throw ApiException.solicitudInvalida("Uno o mas profesionales indicados no existen");
        }
        return profesionales;
    }

    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> ApiException.solicitudInvalida("La categoría indicada no existe"));
    }

    private Producto buscar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> ApiException.noEncontrado("Producto no encontrado"));
    }

    private ProductoResponse aResponse(Producto producto) {
        return ProductoResponse.builder()
                .id(producto.getId())
                .idCategoria(producto.getCategoria().getId())
                .nombreCategoria(producto.getCategoria().getNombre())
                .nombre(producto.getNombre())
                .descripcionCorta(producto.getDescripcionCorta())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .disponible(producto.isDisponible())
                .estado(producto.getEstado())
                .profesionales(producto.getProfesionales().stream()
                        .map(p -> p.getNombres() + " " + p.getApellidos())
                        .collect(Collectors.toList()))
                .build();
    }
}
