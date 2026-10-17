package com.consultorio_obstetra.obstetra.repository;

import com.consultorio_obstetra.obstetra.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByPaciente_Id(Long idPaciente);
    List<Cita> findByProfesional_IdAndFechaHoraBetween(Long idProfesional, java.time.LocalDateTime desde, java.time.LocalDateTime hasta);
}
