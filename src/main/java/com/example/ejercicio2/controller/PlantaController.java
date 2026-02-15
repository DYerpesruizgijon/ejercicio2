package com.example.ejercicio2.controller;

import org.springframework.security.core.Authentication;
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
    public String index(Model model, Authentication authentication) {
        // 1. Enviamos la lista a ambas posibles vistas
        model.addAttribute("plantas", repo.findAllByOrderByIdAsc());

        // 2. Verificamos si el usuario está autenticado
        if (authentication != null && authentication.isAuthenticated()) {
            // 3. Revisamos si tiene el rol de ADMIN
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                return "index-admin"; // Carga index-admin.html si es admin
            }
        }

        // 4. Si no está logueado o es un usuario común, carga la vista normal
        return "index"; // Carga index.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Esto busca el archivo login.html
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("planta", new Planta());
        return "add"; // template para añadir
    }

    @PostMapping("/add")
    public String addPlanta(Planta planta) {
        // 1. Buscamos en la base de datos si ya existe un tipo con ese nombre
        Tipo existente = tipoRepo.findByNombre(planta.getTipo().getNombre());

        if (existente != null) {
            // 2. Si existe, le asignamos a la planta el tipo que YA tiene un ID
            // Esto evita que Hibernate intente crear un tipo duplicado
            planta.setTipo(existente);
        } else {
            // 3. Si no existe, guardamos el tipo nuevo primero
            tipoRepo.save(planta.getTipo());
        }

        // 4. Ahora guardamos la planta con un tipo que ya es "conocido" por la BD
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
        // 1. FUNDAMENTAL: Le ponemos el ID de la URL a la planta 
        // para que Spring sepa que es una EDICIÓN y no un ALTA
        planta.setId(id);

        // 2. Misma lógica que en añadir para el Tipo
        Tipo existente = tipoRepo.findByNombre(planta.getTipo().getNombre());
        if (existente != null) {
            planta.setTipo(existente);
        } else {
            // Si el usuario cambia el tipo por uno que no existe, lo creamos
            tipoRepo.save(planta.getTipo());
        }

        repo.save(planta);
        return "redirect:/";
    }

    //  Mostrar la página de confirmación
    @GetMapping("/confirm-delete/{id}")
    public String mostrarConfirmacion(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        return "confirmar-borrado";
    }

    //  Procesar el borrado con clave
    @PostMapping("/delete/{id}")
    public String eliminarPlanta(@PathVariable Long id) {

        repo.deleteById(id);
        return "redirect:/";
    }
}
