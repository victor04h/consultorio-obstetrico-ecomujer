package com.consultorio_obstetra.obstetra.service;

import com.consultorio_obstetra.obstetra.dto.registro.ConsultaDniResponse;
import com.consultorio_obstetra.obstetra.dto.registro.PersonaDatosDTO;
import com.consultorio_obstetra.obstetra.dto.registro.RegistroRequest;
import com.consultorio_obstetra.obstetra.dto.registro.RegistroResponse;
import com.consultorio_obstetra.obstetra.exception.ApiException;
import com.consultorio_obstetra.obstetra.model.Paciente;
import com.consultorio_obstetra.obstetra.model.Perfil;
import com.consultorio_obstetra.obstetra.model.Persona;
import com.consultorio_obstetra.obstetra.model.Usuario;
import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import com.consultorio_obstetra.obstetra.repository.PacienteRepository;
import com.consultorio_obstetra.obstetra.repository.PerfilRepository;
import com.consultorio_obstetra.obstetra.repository.PersonaRepository;
import com.consultorio_obstetra.obstetra.repository.UsuarioRepository;
import com.consultorio_obstetra.obstetra.service.externo.ExternalIdentityService;
import com.consultorio_obstetra.obstetra.service.externo.PersonaExternaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementa el proceso de Registro del BPM:
 * 1) El paciente indica si sus datos vienen de una fuente externa (API) o
 *    los completa a mano (consultarDni / esta clase no obliga a usarlo).
 * 2) Completa el formulario de registro (correo, contrasena, datos del
 *    paciente y, si es menor de edad segun su fecha de nacimiento, los
 *    datos de su apoderado).
 * 3) Se crea la cuenta de acceso (Usuario) y la ficha de Paciente, y se
 *    envia un correo para validar la cuenta antes de poder iniciar sesion.
 */
@Service
@RequiredArgsConstructor
public class RegistroService {

    private static final String PERFIL_PACIENTE = "Paciente";

    private final ExternalIdentityService externalIdentityService;
    private final PersonaRepository personaRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public ConsultaDniResponse consultarDni(String numeroDocumento) {
        Optional<PersonaExternaDTO> resultado = externalIdentityService.consultarPorDocumento(numeroDocumento);
        if (resultado.isEmpty()) {
            return ConsultaDniResponse.builder()
                    .encontrado(false)
                    .persona(null)
                    .menorDeEdadEstimado(false)
                    .build();
        }
        PersonaExternaDTO persona = resultado.get();
        boolean menor = persona.getFechaNacimiento() != null
                && Period.between(persona.getFechaNacimiento(), LocalDate.now()).getYears() < 18;
        return ConsultaDniResponse.builder()
                .encontrado(true)
                .persona(persona)
                .menorDeEdadEstimado(menor)
                .build();
    }

    @Transactional
    public RegistroResponse completarRegistro(RegistroRequest request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw ApiException.conflicto("Ya existe una cuenta registrada con este correo electronico");
        }

        PersonaDatosDTO datosPaciente = request.getPaciente();
        boolean esMenorDeEdad = esMenorDeEdad(datosPaciente.getFechaNacimiento());

        if (esMenorDeEdad && request.getApoderado() == null) {
            throw ApiException.solicitudInvalida(
                    "El paciente es menor de edad: se requieren los datos del apoderado");
        }
        if (esMenorDeEdad
                && datosPaciente.getNumeroDocumento().equals(request.getApoderado().getNumeroDocumento())) {
            throw ApiException.solicitudInvalida("El apoderado no puede tener el mismo documento que el paciente");
        }

        Persona personaPaciente = obtenerOCrearPersona(datosPaciente);

        if (pacienteRepository.findByPersona_NumeroDocumento(datosPaciente.getNumeroDocumento()).isPresent()) {
            throw ApiException.conflicto("Ya existe un paciente registrado con este numero de documento");
        }

        Persona personaApoderado = null;
        if (esMenorDeEdad) {
            personaApoderado = obtenerOCrearPersona(request.getApoderado());
        }

        Perfil perfilPaciente = perfilRepository.findByNombre(PERFIL_PACIENTE)
                .orElseThrow(() -> ApiException.noEncontrado(
                        "No existe el perfil 'Paciente'. Contacta al administrador."));

        // El titular de la cuenta de acceso es el propio paciente si es
        // mayor de edad, o su apoderado si es menor (ver Paciente.java).
        Persona titularCuenta = esMenorDeEdad ? personaApoderado : personaPaciente;

        String token = UUID.randomUUID().toString();

        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .persona(titularCuenta)
                .perfil(perfilPaciente)
                .correo(request.getCorreo())
                .contrasenaHash(passwordEncoder.encode(request.getContrasena()))
                .estado(EstadoActivo.ACTIVO)
                .correoValidado(false)
                .tokenValidacionCorreo(token)
                .build());

        Paciente paciente = pacienteRepository.save(Paciente.builder()
                .persona(personaPaciente)
                .apoderado(personaApoderado)
                .usuario(usuario)
                .esMenorEdad(esMenorDeEdad)
                .build());

        emailService.enviarCorreoValidacion(usuario.getCorreo(), token);

        return RegistroResponse.builder()
                .idPaciente(paciente.getId())
                .correo(usuario.getCorreo())
                .nombreCompleto(personaPaciente.getNombres() + " " + personaPaciente.getApellidos())
                .esMenorEdad(esMenorDeEdad)
                .mensaje("Registro exitoso. Revisa tu correo para validar la cuenta antes de iniciar sesión.")
                .build();
    }

    @Transactional
    public void validarCorreo(String token) {
        Usuario usuario = usuarioRepository.findByTokenValidacionCorreo(token)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "El token de validación no es válido"));

        usuario.setCorreoValidado(true);
        usuario.setTokenValidacionCorreo(null);
        usuarioRepository.save(usuario);
    }

    private Persona obtenerOCrearPersona(PersonaDatosDTO datos) {
        return personaRepository.findByNumeroDocumento(datos.getNumeroDocumento())
                .map(existente -> {
                    existente.setNombres(datos.getNombres());
                    existente.setApellidos(datos.getApellidos());
                    existente.setFechaNacimiento(datos.getFechaNacimiento());
                    existente.setTelefono(datos.getTelefono());
                    return personaRepository.save(existente);
                })
                .orElseGet(() -> personaRepository.save(Persona.builder()
                        .numeroDocumento(datos.getNumeroDocumento())
                        .nombres(datos.getNombres())
                        .apellidos(datos.getApellidos())
                        .fechaNacimiento(datos.getFechaNacimiento())
                        .telefono(datos.getTelefono())
                        .build()));
    }

    private boolean esMenorDeEdad(LocalDate fechaNacimiento) {
        return fechaNacimiento != null && Period.between(fechaNacimiento, LocalDate.now()).getYears() < 18;
    }
}
