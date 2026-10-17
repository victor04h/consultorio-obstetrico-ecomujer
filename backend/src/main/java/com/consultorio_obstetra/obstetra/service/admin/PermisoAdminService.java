package com.consultorio_obstetra.obstetra.service.admin;

import com.consultorio_obstetra.obstetra.dto.admin.perfil.PermisoRequest;
import com.consultorio_obstetra.obstetra.dto.admin.perfil.PermisoResponse;
import com.consultorio_obstetra.obstetra.exception.ApiException;
import com.consultorio_obstetra.obstetra.model.Permiso;
import com.consultorio_obstetra.obstetra.model.enums.AccionAuditoria;
import com.consultorio_obstetra.obstetra.repository.PermisoRepository;
import com.consultorio_obstetra.obstetra.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/** Catalogo de permisos que luego se asignan a los Perfiles (RBAC). */
@Service
@RequiredArgsConstructor
public class PermisoAdminService {

    private final PermisoRepository permisoRepository;
    private final AuditoriaService auditoriaService;

    public List<PermisoResponse> listar() {
        return permisoRepository.findAll().stream().map(this::aResponse).collect(Collectors.toList());
    }

    @Transactional
    public PermisoResponse crear(PermisoRequest request) {
        // "nombre" lo exige la base de datos oficial (NOT NULL) pero no se
        // pide en el formulario: se genera solo a partir de modulo+accion,
        // igual que en el sembrado inicial (ver DataSeeder.sembrarPermisos).
        Permiso permiso = permisoRepository.save(Permiso.builder()
                .nombre(request.getModulo() + " - " + request.getAccion())
                .modulo(request.getModulo())
                .accion(request.getAccion())
                .descripcion(request.getDescripcion())
                .build());
        auditoriaService.registrar("Permiso", permiso.getId(), AccionAuditoria.CREACION,
                "Permiso creado: " + permiso.getModulo() + ":" + permiso.getAccion());
        return aResponse(permiso);
    }

    @Transactional
    public void eliminar(Long id) {
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> ApiException.noEncontrado("Permiso no encontrado"));
        permisoRepository.delete(permiso);
        auditoriaService.registrar("Permiso", id, AccionAuditoria.ELIMINACION, "Permiso eliminado");
    }

    private PermisoResponse aResponse(Permiso permiso) {
        return PermisoResponse.builder()
                .id(permiso.getId())
                .modulo(permiso.getModulo())
                .accion(permiso.getAccion())
                .descripcion(permiso.getDescripcion())
                .build();
    }
}
