package com.consultorio_obstetra.obstetra.repository;

import com.consultorio_obstetra.obstetra.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findByEntidadAndIdEntidad(String entidad, Long idEntidad);
}
