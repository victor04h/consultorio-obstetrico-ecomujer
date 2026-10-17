package com.consultorio_obstetra.obstetra.repository;

import com.consultorio_obstetra.obstetra.model.Obstetra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObstetraRepository extends JpaRepository<Obstetra, Long> {
}
