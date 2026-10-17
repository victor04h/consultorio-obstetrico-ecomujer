package com.consultorio_obstetra.obstetra.repository;

import com.consultorio_obstetra.obstetra.model.Producto;
import com.consultorio_obstetra.obstetra.model.enums.EstadoActivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoria_Id(Long idCategoria);
    List<Producto> findByEstado(EstadoActivo estado);
}
