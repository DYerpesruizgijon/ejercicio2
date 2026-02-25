package com.example.ejercicio2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ejercicio2.model.Planta;

public interface PlantaRepository extends JpaRepository<Planta, Long> {

    // Añade esta línea para que Postgre ordene por ID
    List<Planta> findAllByOrderByIdAsc();

    List<Planta> findByTipoNombre(String nombre);

    List<Planta> findByCreador(String creador);

    Planta findById(long id);
}
