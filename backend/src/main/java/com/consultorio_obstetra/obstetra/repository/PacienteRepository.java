package com.consultorio_obstetra.obstetra.repository;

import com.consultorio_obstetra.obstetra.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByPersona_NumeroDocumento(String numeroDocumento);
    List<Paciente> findByPersona_NombresContainingIgnoreCaseOrPersona_ApellidosContainingIgnoreCase(String nombres, String apellidos);
}
