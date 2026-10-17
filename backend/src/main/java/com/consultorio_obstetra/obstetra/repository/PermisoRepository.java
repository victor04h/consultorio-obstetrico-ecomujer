package com.consultorio_obstetra.obstetra.repository;

import com.consultorio_obstetra.obstetra.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    Optional<Permiso> findByModuloAndAccion(String modulo, String accion);
}
