package com.consultorio_obstetra.obstetra.repository;

import com.consultorio_obstetra.obstetra.model.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {
    List<HistorialClinico> findByPaciente_IdOrderByFechaRegistroDesc(Long idPaciente);
}
