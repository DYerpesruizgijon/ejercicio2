package com.example.ejercicio2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.ejercicio2.model.Planta;
import com.example.ejercicio2.model.Tipo;
import com.example.ejercicio2.repository.PlantaRepository;
import com.example.ejercicio2.repository.TipoRepository;

@Controller
public class PlantaController {

    private final PlantaRepository repo;
    private final TipoRepository tipoRepo;

    public PlantaController(PlantaRepository repo, TipoRepository tipoRepo) {
        this.repo = repo;
        this.tipoRepo = tipoRepo;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("plantas", repo.findAll());
        return "index"; // nombre del template Thymeleaf
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("planta", new Planta());
        return "add"; // template para añadir
    }

    @PostMapping("/add")
    public String addPlanta(Planta planta) {
        //evitar duplicados
        Tipo existente = tipoRepo.findByNombre(planta.getTipo().getNombre());
        if (existente != null) {
            planta.setTipo(existente);
        }
        
        repo.save(planta);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("planta", repo.findById(id).get());
        return "edit"; // template para editar
    }

    @PostMapping("/edit/{id}")
    public String editPlanta(@PathVariable Long id, Planta planta) {
        planta.setId(id);
        
        //evitar duplicados
        Tipo existente = tipoRepo.findByNombre(planta.getTipo().getNombre());
        if (existente != null) {
            planta.setTipo(existente);
        }

        repo.save(planta);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deletePlanta(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/";
    }
}
