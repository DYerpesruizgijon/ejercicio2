package com.example.ejercicio2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ejercicio2.model.Planta;

public interface PlantaRepository extends JpaRepository<Planta, Long> {

    // buscamos por el atributo 'nombre' que está DENTRO del objeto 'tipo'
    List<Planta> findByTipoNombre(String nombre);

    Planta findById(long id);
}
