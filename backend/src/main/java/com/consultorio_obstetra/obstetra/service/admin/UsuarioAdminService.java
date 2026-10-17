package com.consultorio_obstetra.obstetra.service.admin;

import com.consultorio_obstetra.obstetra.dto.admin.usuario.UsuarioInternoRequest;
import com.consultorio_obstetra.obstetra.dto.admin.usuario.UsuarioInternoResponse;
import com.consultorio_obstetra.obstetra.exception.ApiException;
import com.consultorio_obstetra.obstetra.model.Perfil;
import com.consultorio_obstetra.obstetra.model.Persona;
import com.consultorio_obstetra.obstetra.model.Profesional;
import com.consultorio_obstetra.obstetra.model.Usuario;
import com.consultorio_obstetra.obstetra.model.enums.AccionAuditoria;
import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import com.consultorio_obstetra.obstetra.repository.PerfilRepository;
import com.consultorio_obstetra.obstetra.repository.PersonaRepository;
import com.consultorio_obstetra.obstetra.repository.ProfesionalRepository;
import com.consultorio_obstetra.obstetra.repository.UsuarioRepository;
import com.consultorio_obstetra.obstetra.security.SecurityUtils;
import com.consultorio_obstetra.obstetra.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioAdminService {

    private static final String PERFIL_PACIENTE = "Paciente";
    private static final String PERFIL_OBSTETRA = "Obstetra";

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PersonaRepository personaRepository;
    private final ProfesionalRepository profesionalRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public List<UsuarioInternoResponse> listar() {
        return usuarioRepository.findAll().stream()
                .filter(u -> !PERFIL_PACIENTE.equalsIgnoreCase(u.getPerfil().getNombre()))
                .map(this::aResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioInternoResponse crear(UsuarioInternoRequest request) {

        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw ApiException.conflicto("Ya existe una cuenta con ese correo");
        }

        Perfil perfil = perfilRepository.findById(request.getIdPerfil())
                .orElseThrow(() ->
                        ApiException.solicitudInvalida("El perfil indicado no existe"));

        Profesional profesional = null;

        if (PERFIL_OBSTETRA.equalsIgnoreCase(perfil.getNombre())) {

            if (request.getIdProfesional() == null) {
                throw ApiException.solicitudInvalida(
                        "Selecciona a que Profesional (obstetra) corresponde esta cuenta");
            }

            profesional = profesionalRepository.findById(request.getIdProfesional())
                    .orElseThrow(() ->
                            ApiException.solicitudInvalida(
                                    "El profesional indicado no existe"));
        }

        Persona persona = personaRepository.save(
                Persona.builder()
                        .numeroDocumento(
                                "STAFF-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8)
                                        .toUpperCase()
                        )
                        .nombres(request.getNombres())
                        .apellidos(request.getApellidos())
                        .build()
        );

        Usuario usuario = usuarioRepository.save(
                Usuario.builder()
                        .persona(persona)
                        .perfil(perfil)
                        .profesional(profesional)
                        .correo(request.getCorreo())
                        .contrasenaHash(
                                passwordEncoder.encode(request.getContrasena())
                        )
                        .estado(EstadoActivo.ACTIVO)
                        .correoValidado(true)
                        .build()
        );

        auditoriaService.registrar(
                "Usuario",
                usuario.getId(),
                AccionAuditoria.CREACION,
                "Cuenta interna creada: "
                        + usuario.getCorreo()
                        + " ("
                        + perfil.getNombre()
                        + ")"
        );

        return aResponse(usuario);
    }

    @Transactional
    public UsuarioInternoResponse cambiarEstado(
            Long id,
            EstadoActivo estado) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        ApiException.noEncontrado("Cuenta no encontrada"));

        /*
         * Guardamos el ID en una variable independiente porque
         * posteriormente la variable "usuario" se reasigna al
         * resultado de usuarioRepository.save(usuario).
         */
        Long idUsuario = usuario.getId();

        SecurityUtils.usuarioActual().ifPresent(actual -> {

            if (actual.getId().equals(idUsuario)
                    && estado == EstadoActivo.INACTIVO) {

                throw ApiException.solicitudInvalida(
                        "No puedes desactivar tu propia cuenta");
            }
        });

        usuario.setEstado(estado);

        usuario = usuarioRepository.save(usuario);

        auditoriaService.registrar(
                "Usuario",
                usuario.getId(),
                AccionAuditoria.INACTIVACION,
                "Cuenta "
                        + usuario.getCorreo()
                        + " -> "
                        + estado
        );

        return aResponse(usuario);
    }

    private UsuarioInternoResponse aResponse(Usuario usuario) {

        return UsuarioInternoResponse.builder()
                .id(usuario.getId())
                .nombres(usuario.getPersona().getNombres())
                .apellidos(usuario.getPersona().getApellidos())
                .correo(usuario.getCorreo())
                .idPerfil(usuario.getPerfil().getId())
                .nombrePerfil(usuario.getPerfil().getNombre())
                .idProfesional(
                        usuario.getProfesional() != null
                                ? usuario.getProfesional().getId()
                                : null
                )
                .nombreProfesional(
                        usuario.getProfesional() != null
                                ? usuario.getProfesional().getNombres()
                                        + " "
                                        + usuario.getProfesional().getApellidos()
                                : null
                )
                .estado(usuario.getEstado())
                .build();
    }
}