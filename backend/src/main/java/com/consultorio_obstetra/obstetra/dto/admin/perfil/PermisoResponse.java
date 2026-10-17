package com.consultorio_obstetra.obstetra.dto.admin.perfil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermisoResponse {
    private Long id;
    private String modulo;
    private String accion;
    private String descripcion;
}
