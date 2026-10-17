package com.consultorio_obstetra.obstetra.model;

import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Cuenta de acceso (login). El titular de la cuenta es la Persona asociada:
 * el propio paciente si es mayor de edad, o su apoderado si es menor
 * (ver proceso de Registro).
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    /**
     * Estas 3 columnas (dni, nombre_completo, fecha_nacimiento) las exige la
     * tabla `usuario` de la base de datos oficial (son NOT NULL). El sistema
     * ya guarda estos mismos datos en Persona (numero_documento/nombres/
     * apellidos/fecha_nacimiento); aqui se replican automaticamente al
     * guardar (ver preInsertUpdate) solo para satisfacer esas columnas, no
     * se editan a mano en ningun formulario.
     */
    @Column(length = 15)
    private String dni;

    @Column(name = "nombre_completo", length = 150)
    private String nombreCompleto;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_perfil", nullable = false)
    private Perfil perfil;

    /**
     * Solo se usa cuando esta cuenta pertenece a un perfil "Obstetra": indica
     * a cual Profesional corresponde este login, para poder filtrar sus
     * propias citas e historial clinico. Nula para el resto de perfiles.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profesional")
    private Profesional profesional;

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @Column(name = "contrasena_hash", nullable = false)
    private String contrasenaHash;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoActivo estado = EstadoActivo.ACTIVO;

    @Column(name = "correo_validado")
    @Builder.Default
    private boolean correoValidado = false;

    @Column(name = "token_validacion_correo", length = 64)
    private String tokenValidacionCorreo;

    @Column(name = "intentos_fallidos")
    @Builder.Default
    private int intentosFallidos = 0;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private LocalDateTime fechaModificacion;

    @PrePersist
    public void prePersist() {
        sincronizarDatosDePersona();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now();
        sincronizarDatosDePersona();
    }

    /**
     * Copia nombres/documento/fecha de nacimiento desde la Persona vinculada
     * hacia las columnas propias de `usuario` (dni, nombre_completo,
     * fecha_nacimiento) que exige la base de datos oficial. Si la Persona no
     * tiene fecha de nacimiento (caso de cuentas de personal interno, que no
     * la piden), se deja una fecha de referencia en vez de null, porque la
     * columna es NOT NULL.
     */
    private void sincronizarDatosDePersona() {
        if (persona == null) {
            return;
        }
        this.dni = persona.getNumeroDocumento();
        this.nombreCompleto = (persona.getNombres() + " " + persona.getApellidos()).trim();
        this.fechaNacimiento = persona.getFechaNacimiento() != null
                ? persona.getFechaNacimiento()
                : LocalDate.of(2000, 1, 1);
    }
}
