package com.consultorio_obstetra.obstetra.service.admin;

import com.consultorio_obstetra.obstetra.dto.admin.categoria.CategoriaRequest;
import com.consultorio_obstetra.obstetra.dto.admin.categoria.CategoriaResponse;
import com.consultorio_obstetra.obstetra.exception.ApiException;
import com.consultorio_obstetra.obstetra.model.Categoria;
import com.consultorio_obstetra.obstetra.model.enums.AccionAuditoria;
import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import com.consultorio_obstetra.obstetra.repository.CategoriaRepository;
import com.consultorio_obstetra.obstetra.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/** Modulo "Categoría" del BPM. */
@Service
@RequiredArgsConstructor
public class CategoriaAdminService {

    private final CategoriaRepository categoriaRepository;
    private final AuditoriaService auditoriaService;

    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream().map(this::aResponse).collect(Collectors.toList());
    }

    public List<CategoriaResponse> listarActivas() {
        return categoriaRepository.findAll().stream()
                .filter(c -> c.getEstado() == EstadoActivo.ACTIVO)
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    public CategoriaResponse obtener(Long id) {
        return aResponse(buscar(id));
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        if (categoriaRepository.findByNombre(request.getNombre()).isPresent()) {
            throw ApiException.conflicto("Ya existe una categoría con ese nombre");
        }
        Categoria categoria = categoriaRepository.save(Categoria.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .estado(EstadoActivo.ACTIVO)
                .build());
        auditoriaService.registrar("Categoría", categoria.getId(), AccionAuditoria.CREACION,
                "Categoría creada: " + categoria.getNombre());
        return aResponse(categoria);
    }

    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscar(id);
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        categoria = categoriaRepository.save(categoria);
        auditoriaService.registrar("Categoría", categoria.getId(), AccionAuditoria.EDICION,
                "Categoría editada: " + categoria.getNombre());
        return aResponse(categoria);
    }

    @Transactional
    public CategoriaResponse cambiarEstado(Long id, EstadoActivo estado) {
        Categoria categoria = buscar(id);
        categoria.setEstado(estado);
        categoria = categoriaRepository.save(categoria);
        auditoriaService.registrar("Categoría", categoria.getId(), AccionAuditoria.INACTIVACION,
                "Categoría " + categoria.getNombre() + " -> " + estado);
        return aResponse(categoria);
    }

    private Categoria buscar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> ApiException.noEncontrado("Categoría no encontrada"));
    }

    private CategoriaResponse aResponse(Categoria categoria) {
        return CategoriaResponse.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .estado(categoria.getEstado())
                .build();
    }
}
