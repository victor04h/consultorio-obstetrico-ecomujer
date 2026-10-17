package com.consultorio_obstetra.obstetra.repository;

import com.consultorio_obstetra.obstetra.model.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {
    List<Calificacion> findByProfesional_Id(Long idProfesional);
}
