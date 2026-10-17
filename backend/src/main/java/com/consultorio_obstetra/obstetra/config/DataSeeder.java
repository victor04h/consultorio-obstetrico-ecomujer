package com.consultorio_obstetra.obstetra.config;

import com.consultorio_obstetra.obstetra.model.Obstetra;
import com.consultorio_obstetra.obstetra.model.Perfil;
import com.consultorio_obstetra.obstetra.model.Permiso;
import com.consultorio_obstetra.obstetra.model.Persona;
import com.consultorio_obstetra.obstetra.model.Profesional;
import com.consultorio_obstetra.obstetra.model.Sede;
import com.consultorio_obstetra.obstetra.model.Usuario;
import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import com.consultorio_obstetra.obstetra.repository.ObstetraRepository;
import com.consultorio_obstetra.obstetra.repository.PerfilRepository;
import com.consultorio_obstetra.obstetra.repository.PermisoRepository;
import com.consultorio_obstetra.obstetra.repository.PersonaRepository;
import com.consultorio_obstetra.obstetra.repository.ProfesionalRepository;
import com.consultorio_obstetra.obstetra.repository.SedeRepository;
import com.consultorio_obstetra.obstetra.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Crea, si no existen, el catalogo de permisos, los perfiles base (roles) y
 * un usuario Administrador de arranque para poder entrar al dashboard la
 * primera vez.
 *
 * Usuario: admin@consultorio.com
 * Clave:   Admin123!
 * (cambiala apenas entres la primera vez).
 *
 * Modulos del catalogo de permisos (uno por seccion del panel; la accion es
 * siempre "ACCESO" porque hoy el control es a nivel de seccion completa, no
 * por operacion individual dentro de ella):
 * PERFILES, USUARIOS, CATEGORIAS, PRODUCTOS, PROFESIONALES, PACIENTES,
 * CITAS, REPROGRAMACIONES, HISTORIAL_CLINICO, CALIFICACIONES.
 *
 * "Inicio" (el resumen del dashboard) no requiere permiso: cualquier cuenta
 * que inicie sesion en el panel lo puede ver.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private static final String ACCION_ACCESO = "ACCESO";

    private static final List<String> MODULOS = List.of(
            "PERFILES", "USUARIOS", "CATEGORIAS", "PRODUCTOS", "PROFESIONALES",
            "PACIENTES", "CITAS", "REPROGRAMACIONES", "HISTORIAL_CLINICO", "CALIFICACIONES"
    );

    private final PerfilRepository perfilRepository;
    private final PermisoRepository permisoRepository;
    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProfesionalRepository profesionalRepository;
    private final ObstetraRepository obstetraRepository;
    private final SedeRepository sedeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        alinearEnumsConBaseDeDatosOficial();
        sembrarSedePorDefecto();
        respaldarObstetrasFaltantes();

        Set<Permiso> todosLosPermisos = sembrarPermisos();

        Perfil administrador = perfilRepository.findByNombre("Administrador")
                .orElseGet(() -> perfilRepository.save(Perfil.builder()
                        .nombre("Administrador")
                        .descripcion("Acceso completo al sistema de gestion")
                        .estado(EstadoActivo.ACTIVO)
                        .build()));
        // El Administrador siempre debe tener el catalogo completo de permisos
        // marcado. Se completa lo que falte (no solo si esta vacio) para que,
        // cuando se agregue un modulo nuevo en una actualizacion futura, el
        // Administrador lo reciba automaticamente sin perder nada.
        if (!administrador.getPermisos().containsAll(todosLosPermisos)) {
            Set<Permiso> permisosActualizados = new HashSet<>(administrador.getPermisos());
            permisosActualizados.addAll(todosLosPermisos);
            administrador.setPermisos(permisosActualizados);
            administrador = perfilRepository.save(administrador);
        }

        perfilRepository.findByNombre("Paciente")
                .orElseGet(() -> perfilRepository.save(Perfil.builder()
                        .nombre("Paciente")
                        .descripcion("Paciente que usa el sitio web de citas")
                        .estado(EstadoActivo.ACTIVO)
                        .build()));

        Perfil secretaria = sembrarPerfilConPermisosPorDefecto(
                "Secretaria",
                "Personal de recepcion: agenda citas, atiende pacientes y mantiene el catalogo",
                Set.of("PACIENTES", "CITAS", "REPROGRAMACIONES", "CATEGORIAS", "PRODUCTOS", "PROFESIONALES", "CALIFICACIONES")
        );

        // Nota: la pantalla de Citas carga en una sola tanda Pacientes, Productos
        // y Profesionales junto con las citas (para los combos del formulario), y
        // la de Historial Clinico hace lo mismo con Pacientes y Profesionales. Por
        // eso el Obstetra necesita esos modulos marcados aunque solo termine
        // viendo sus propias citas/historiales/reprogramaciones (el filtrado es
        // por datos, no por pantalla). El administrador puede ajustar esto luego
        // desde Perfiles.
        Perfil obstetra = sembrarPerfilConPermisosPorDefecto(
                "Obstetra",
                "Profesional médico: ve y atiende sus propias citas e historiales clínicos",
                Set.of("CITAS", "REPROGRAMACIONES", "HISTORIAL_CLINICO", "CALIFICACIONES", "PROFESIONALES", "PACIENTES", "PRODUCTOS")
        );

        if (!usuarioRepository.existsByCorreo("admin@consultorio.com")) {
            Persona persona = personaRepository.save(Persona.builder()
                    .numeroDocumento("ADMIN-0001")
                    .nombres("Administrador")
                    .apellidos("del Sistema")
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .persona(persona)
                    .perfil(administrador)
                    .correo("admin@consultorio.com")
                    .contrasenaHash(passwordEncoder.encode("Admin123!"))
                    .estado(EstadoActivo.ACTIVO)
                    .correoValidado(true)
                    .build());
        }

        // Cuentas de prueba para poder entrar como Secretaria y como Obstetra sin
        // pasar por el modulo Usuarios. Se crean una sola vez (si el correo ya
        // existe, se asume que el usuario ya las tiene o las administra el mismo).
        if (!usuarioRepository.existsByCorreo("secretaria@consultorio.com")) {
            Persona personaSecretaria = personaRepository.save(Persona.builder()
                    .numeroDocumento("STAFF-0001")
                    .nombres("Secretaria")
                    .apellidos("de Prueba")
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .persona(personaSecretaria)
                    .perfil(secretaria)
                    .correo("secretaria@consultorio.com")
                    .contrasenaHash(passwordEncoder.encode("Secretaria123!"))
                    .estado(EstadoActivo.ACTIVO)
                    .correoValidado(true)
                    .build());
        }

        if (!usuarioRepository.existsByCorreo("obstetra@consultorio.com")) {
            // El Obstetra necesita un Profesional propio vinculado para que el
            // filtrado de "solo mis datos" tenga algo que mostrar en la demo.
            Profesional profesionalDemo = profesionalRepository.save(Profesional.builder()
                    .nombres("Obstetra")
                    .apellidos("de Prueba")
                    .especialidad("Obstetricia")
                    .estado(EstadoActivo.ACTIVO)
                    .build());

            // Espejo en la tabla `obstetra` de la base de datos oficial (ver
            // comentario de Obstetra.java): sin esto, ninguna cita ni entrada
            // de historial podria crearse para este profesional de prueba.
            obstetraRepository.save(Obstetra.builder()
                    .id(profesionalDemo.getId())
                    .nombreCompleto(profesionalDemo.getNombres() + " " + profesionalDemo.getApellidos())
                    .estado(EstadoActivo.ACTIVO)
                    .build());

            Persona personaObstetra = personaRepository.save(Persona.builder()
                    .numeroDocumento("STAFF-0002")
                    .nombres("Obstetra")
                    .apellidos("de Prueba")
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .persona(personaObstetra)
                    .perfil(obstetra)
                    .profesional(profesionalDemo)
                    .correo("obstetra@consultorio.com")
                    .contrasenaHash(passwordEncoder.encode("Obstetra123!"))
                    .estado(EstadoActivo.ACTIVO)
                    .correoValidado(true)
                    .build());
        }
    }

    /** Crea cualquier permiso del catalogo que todavia no exista y devuelve el catalogo completo. */
    private Set<Permiso> sembrarPermisos() {
        Set<Permiso> permisos = new HashSet<>();
        for (String modulo : MODULOS) {
            Permiso permiso = permisoRepository.findByModuloAndAccion(modulo, ACCION_ACCESO)
                    .orElseGet(() -> permisoRepository.save(Permiso.builder()
                            .nombre("Acceso a " + modulo.replace("_", " "))
                            .modulo(modulo)
                            .accion(ACCION_ACCESO)
                            .descripcion("Acceso a la seccion " + modulo.replace("_", " "))
                            .build()));
            permisos.add(permiso);
        }
        return permisos;
    }

    /**
     * Crea la "Sede Principal" si todavia no existe ninguna sede. El sistema
     * no tiene (por ahora) un modulo para administrar varias sedes; esta es
     * la unica que se asigna a todas las citas (ver Cita.java / Sede.java).
     */
    private void sembrarSedePorDefecto() {
        if (sedeRepository.count() == 0) {
            sedeRepository.save(Sede.builder()
                    .nombre("Sede Principal")
                    .build());
        }
    }

    /**
     * Si ya tenias Profesionales creados ANTES de esta actualizacion, todavia
     * no tienen su espejo en la tabla `obstetra` (ver Obstetra.java) porque
     * antes no existia esa sincronizacion. Sin esto, crear una cita o una
     * entrada de historial para ellos fallaria hasta editarlos manualmente
     * una vez; se completa solo, una sola vez por profesional.
     */
    private void respaldarObstetrasFaltantes() {
        for (Profesional profesional : profesionalRepository.findAll()) {
            if (obstetraRepository.existsById(profesional.getId())) {
                continue;
            }
            obstetraRepository.save(Obstetra.builder()
                    .id(profesional.getId())
                    .nombreCompleto((profesional.getNombres() + " " + profesional.getApellidos()).trim())
                    .aniosExperiencia(profesional.getAñosExperiencia())
                    .descripcion(profesional.getReseña())
                    .estado(profesional.getEstado())
                    .build());
        }
    }

    /**
     * La base de datos oficial (base_obstetra) trae algunas columnas `estado`
     * definidas como ENUM con valores propios de un diseño anterior
     * (categoria/producto: 'publicada'/'no_publicada', 'publicado'/
     * 'no_publicado'; cita: 'pendiente_pago'/'confirmada'/'cancelada'/
     * 'no_asistio') que no coinciden con los que usa este sistema
     * (ACTIVO/INACTIVO, y los 6 estados propios de Cita). Como
     * ddl-auto=update nunca modifica una columna que ya existe, se ajustan
     * aqui una sola vez al arrancar, con SQL nativo e idempotente (correrlo
     * de nuevo no hace daño). Si tenias citas/categorias/productos de PRUEBA
     * guardados con los valores viejos, su estado quedara en blanco despues
     * de este cambio (es normal, solo pasa la primera vez).
     */
    private void alinearEnumsConBaseDeDatosOficial() {
        ejecutarSiNoFalla("ALTER TABLE categoria MODIFY estado ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO'");
        ejecutarSiNoFalla("ALTER TABLE producto MODIFY estado ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO'");
        ejecutarSiNoFalla("ALTER TABLE cita MODIFY estado "
                + "ENUM('AGENDADA','PAGADA','CONFIRMADA','REASIGNADA','RECHAZADA','CANCELADA') DEFAULT 'AGENDADA'");
    }

    private void ejecutarSiNoFalla(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception ex) {
            log.warn("No se pudo ajustar el esquema con '{}': {}", sql, ex.getMessage());
        }
    }

    /**
     * Crea el perfil si no existe, con el set de modulos por defecto ya
     * marcado, y devuelve el perfil (el ya existente o el recien creado) para
     * poder usarlo al crear las cuentas de prueba de mas abajo.
     */
    private Perfil sembrarPerfilConPermisosPorDefecto(String nombre, String descripcion, Set<String> modulosPorDefecto) {
        return perfilRepository.findByNombre(nombre).orElseGet(() -> {
            Set<Permiso> permisos = new HashSet<>();
            for (String modulo : modulosPorDefecto) {
                permisoRepository.findByModuloAndAccion(modulo, ACCION_ACCESO).ifPresent(permisos::add);
            }
            return perfilRepository.save(Perfil.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .estado(EstadoActivo.ACTIVO)
                    .permisos(permisos)
                    .build());
        });
    }
}
