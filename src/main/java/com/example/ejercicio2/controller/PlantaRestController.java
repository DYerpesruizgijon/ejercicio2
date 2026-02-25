package com.example.ejercicio2.controller;

import com.example.ejercicio2.model.Planta;
import com.example.ejercicio2.repository.PlantaRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/plantas") //  propia API REST
public class PlantaRestController {

    private final PlantaRepository repo;

    public PlantaRestController(PlantaRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Planta> obtenerTodas() {
        return repo.findAll(); // Esto devuelve un JSON, perfecto para Swagger
    }
}