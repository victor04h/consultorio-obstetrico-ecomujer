package com.consultorio_obstetra.obstetra.repository;

import com.consultorio_obstetra.obstetra.model.CitaReasignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CitaReasignacionRepository extends JpaRepository<CitaReasignacion, Long> {
    List<CitaReasignacion> findByCita_Id(Long idCita);

    /** Historial completo de reprogramaciones (todas las citas), mas recientes primero. */
    List<CitaReasignacion> findAllByOrderByFechaSolicitudDesc();
}
