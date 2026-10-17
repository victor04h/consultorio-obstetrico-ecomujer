package com.consultorio_obstetra.obstetra.repository;

import com.consultorio_obstetra.obstetra.model.Profesional;
import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {
    List<Profesional> findByEstado(EstadoActivo estado);
}
