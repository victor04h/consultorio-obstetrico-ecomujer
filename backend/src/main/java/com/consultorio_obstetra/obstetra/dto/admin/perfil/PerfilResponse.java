package com.consultorio_obstetra.obstetra.dto.admin.perfil;

import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private EstadoActivo estado;
    private List<PermisoResponse> permisos;
}
