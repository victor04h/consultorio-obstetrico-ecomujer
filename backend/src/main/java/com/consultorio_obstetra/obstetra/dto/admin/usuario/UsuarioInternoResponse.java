package com.consultorio_obstetra.obstetra.dto.admin.usuario;

import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioInternoResponse {
    private Long id;
    private String nombres;
    private String apellidos;
    private String correo;
    private Long idPerfil;
    private String nombrePerfil;
    private Long idProfesional;
    private String nombreProfesional;
    private EstadoActivo estado;
}
