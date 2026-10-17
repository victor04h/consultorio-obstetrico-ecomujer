package com.consultorio_obstetra.obstetra.service.externo;

import java.util.Optional;

/**
 * Punto de integracion con la fuente externa de identidad que el proceso de
 * Registro consulta cuando el paciente indica que sus datos "vienen de una
 * fuente externa (API)". Se define como interfaz para poder reemplazar la
 * implementacion simulada (MockExternalIdentityService) por un cliente real
 * (por ejemplo RENIEC/SUNAT u otro proveedor) sin tocar el resto del flujo.
 */
public interface ExternalIdentityService {

    /**
     * @param numeroDocumento documento a consultar
     * @return los datos encontrados, o Optional.vacio() si la fuente externa
     *         no tiene registro (en ese caso el usuario completa el
     *         formulario manualmente, como contempla el BPM).
     */
    Optional<PersonaExternaDTO> consultarPorDocumento(String numeroDocumento);
}
