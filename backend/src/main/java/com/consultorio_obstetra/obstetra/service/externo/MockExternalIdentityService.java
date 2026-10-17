package com.consultorio_obstetra.obstetra.service.externo;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Implementacion SIMULADA de ExternalIdentityService para fines academicos
 * y de demostracion, ya que el proyecto no cuenta con credenciales de una
 * fuente de identidad real (por ejemplo RENIEC). Genera datos genericos y
 * deterministicos a partir del numero de documento, sin usar nombres reales
 * de ninguna persona.
 *
 * Para producción, reemplazar este bean por un cliente HTTP real que
 * implemente ExternalIdentityService y consuma el proveedor definitivo.
 */
@Service
public class MockExternalIdentityService implements ExternalIdentityService {

    private static final String[] NOMBRES = {
            "Ana", "Luis", "Carla", "Jorge", "Maria", "Pedro", "Rosa", "Diego", "Elena", "Victor"
    };
    private static final String[] APELLIDOS = {
            "Flores", "Ramos", "Vega", "Quispe", "Rojas", "Salinas", "Torres", "Medina", "Castro", "Nunez"
    };

    @Override
    public Optional<PersonaExternaDTO> consultarPorDocumento(String numeroDocumento) {
        if (numeroDocumento == null || !numeroDocumento.matches("\\d{8}")) {
            return Optional.empty();
        }

        int semilla = Math.abs(numeroDocumento.hashCode());
        String nombres = NOMBRES[semilla % NOMBRES.length];
        String apellidos = APELLIDOS[(semilla / NOMBRES.length) % APELLIDOS.length];
        int edadSimulada = 5 + (semilla % 70); // permite ver tambien el caso "menor de edad"
        LocalDate fechaNacimiento = LocalDate.now().minusYears(edadSimulada).minusDays(semilla % 365);

        return Optional.of(PersonaExternaDTO.builder()
                .numeroDocumento(numeroDocumento)
                .nombres(nombres)
                .apellidos(apellidos)
                .fechaNacimiento(fechaNacimiento)
                .build());
    }
}
