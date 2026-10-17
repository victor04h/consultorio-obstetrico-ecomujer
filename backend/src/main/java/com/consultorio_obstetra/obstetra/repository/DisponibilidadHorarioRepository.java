package com.consultorio_obstetra.obstetra.repository;

import com.consultorio_obstetra.obstetra.model.DisponibilidadHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisponibilidadHorarioRepository extends JpaRepository<DisponibilidadHorario, Long> {
    List<DisponibilidadHorario> findByProfesional_Id(Long idProfesional);
}
