package com.consultorio_obstetra.obstetra.service;

import com.consultorio_obstetra.obstetra.model.Auditoria;
import com.consultorio_obstetra.obstetra.model.enums.AccionAuditoria;
import com.consultorio_obstetra.obstetra.repository.AuditoriaRepository;
import com.consultorio_obstetra.obstetra.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Registra el rastro de auditoria (tabla "auditoria" del BPM) de las
 * operaciones administrativas: quien hizo que, sobre que entidad y cuando.
 */
@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public void registrar(String entidad, Long idEntidad, AccionAuditoria accion, String detalle) {
        Auditoria auditoria = Auditoria.builder()
                .usuario(SecurityUtils.usuarioActual().orElse(null))
                .entidad(entidad)
                .idEntidad(idEntidad)
                .accion(accion)
                .detalle(detalle)
                .build();
        auditoriaRepository.save(auditoria);
    }
}
