package com.example.ejercicio2.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.ejercicio2.model.Tipo;

public interface TipoRepository extends CrudRepository<Tipo, Long> {

    // Buscamos por nombre y devolvemos el objeto directamente
    Tipo findByNombre(String nombre);
}
