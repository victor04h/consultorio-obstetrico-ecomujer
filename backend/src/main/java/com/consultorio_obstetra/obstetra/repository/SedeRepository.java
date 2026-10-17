package com.consultorio_obstetra.obstetra.repository;

import com.consultorio_obstetra.obstetra.model.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Long> {
    Optional<Sede> findFirstByOrderByIdAsc();
}
