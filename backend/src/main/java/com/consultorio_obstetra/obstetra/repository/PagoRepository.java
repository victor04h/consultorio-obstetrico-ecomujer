package com.consultorio_obstetra.obstetra.repository;

import com.consultorio_obstetra.obstetra.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByCita_Id(Long idCita);
}
