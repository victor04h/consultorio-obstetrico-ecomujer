package com.consultorio_obstetra.obstetra.service.admin;

import com.consultorio_obstetra.obstetra.dto.admin.perfil.PerfilRequest;
import com.consultorio_obstetra.obstetra.dto.admin.perfil.PerfilResponse;
import com.consultorio_obstetra.obstetra.dto.admin.perfil.PermisoResponse;
import com.consultorio_obstetra.obstetra.exception.ApiException;
import com.consultorio_obstetra.obstetra.model.Perfil;
import com.consultorio_obstetra.obstetra.model.Permiso;
import com.consultorio_obstetra.obstetra.model.enums.AccionAuditoria;
import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import com.consultorio_obstetra.obstetra.repository.PerfilRepository;
import com.consultorio_obstetra.obstetra.repository.PermisoRepository;
import com.consultorio_obstetra.obstetra.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Modulo "Perfiles" del BPM: administra los roles del sistema y los
 * permisos que llevan asociados.
 */
@Service
@RequiredArgsConstructor
public class PerfilAdminService {

    private final PerfilRepository perfilRepository;
    private final PermisoRepository permisoRepository;
    private final AuditoriaService auditoriaService;

    public List<PerfilResponse> listar() {
        return perfilRepository.findAll().stream().map(this::aResponse).collect(Collectors.toList());
    }

    public PerfilResponse obtener(Long id) {
        return aResponse(buscar(id));
    }

    @Transactional
    public PerfilResponse crear(PerfilRequest request) {
        if (perfilRepository.findByNombre(request.getNombre()).isPresent()) {
            throw ApiException.conflicto("Ya existe un perfil con ese nombre");
        }
        Perfil perfil = Perfil.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .estado(EstadoActivo.ACTIVO)
                .permisos(resolverPermisos(request.getPermisoIds()))
                .build();
        perfil = perfilRepository.save(perfil);
        auditoriaService.registrar("Perfil", perfil.getId(), AccionAuditoria.CREACION,
                "Perfil creado: " + perfil.getNombre());
        return aResponse(perfil);
    }

    @Transactional
    public PerfilResponse actualizar(Long id, PerfilRequest request) {
        Perfil perfil = buscar(id);
        perfil.setNombre(request.getNombre());
        perfil.setDescripcion(request.getDescripcion());
        perfil.setPermisos(resolverPermisos(request.getPermisoIds()));
        perfil = perfilRepository.save(perfil);
        auditoriaService.registrar("Perfil", perfil.getId(), AccionAuditoria.EDICION,
                "Perfil editado: " + perfil.getNombre());
        return aResponse(perfil);
    }

    @Transactional
    public PerfilResponse cambiarEstado(Long id, EstadoActivo estado) {
        Perfil perfil = buscar(id);
        perfil.setEstado(estado);
        perfil = perfilRepository.save(perfil);
        auditoriaService.registrar("Perfil", perfil.getId(), AccionAuditoria.INACTIVACION,
                "Perfil " + perfil.getNombre() + " -> " + estado);
        return aResponse(perfil);
    }

    private Set<Permiso> resolverPermisos(Set<Long> permisoIds) {
        if (permisoIds == null || permisoIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Permiso> permisos = new HashSet<>(permisoRepository.findAllById(permisoIds));
        if (permisos.size() != permisoIds.size()) {
            throw ApiException.solicitudInvalida("Uno o mas permisos indicados no existen");
        }
        return permisos;
    }

    private Perfil buscar(Long id) {
        return perfilRepository.findById(id)
                .orElseThrow(() -> ApiException.noEncontrado("Perfil no encontrado"));
    }

    private PerfilResponse aResponse(Perfil perfil) {
        return PerfilResponse.builder()
                .id(perfil.getId())
                .nombre(perfil.getNombre())
                .descripcion(perfil.getDescripcion())
                .estado(perfil.getEstado())
                .permisos(perfil.getPermisos().stream()
                        .map(p -> PermisoResponse.builder()
                                .id(p.getId())
                                .modulo(p.getModulo())
                                .accion(p.getAccion())
                                .descripcion(p.getDescripcion())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
