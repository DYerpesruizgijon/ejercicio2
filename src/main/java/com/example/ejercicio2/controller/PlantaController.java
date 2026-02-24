package com.example.ejercicio2.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.ejercicio2.Service.CloudinaryService;
import com.example.ejercicio2.model.Planta;
import com.example.ejercicio2.model.Tipo;
import com.example.ejercicio2.repository.PlantaRepository;
import com.example.ejercicio2.repository.TipoRepository;

@Controller
public class PlantaController {

    private final PlantaRepository repo;
    private final TipoRepository tipoRepo;
    private final CloudinaryService cloudinaryService;

    public PlantaController(PlantaRepository repo, TipoRepository tipoRepo, CloudinaryService cloudinaryService) {
        this.repo = repo;
        this.tipoRepo = tipoRepo;
        this.cloudinaryService = cloudinaryService;
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
        model.addAttribute("todosLosTipos", tipoRepo.findAll());
        return "add"; // template para añadir
    }

    @PostMapping("/add")
    public String addPlanta(Planta planta, @RequestParam("imagenFile") MultipartFile imagenFile) {
        try {
            //  Si el usuario subió una imagen, la enviamos a Cloudinary
            if (imagenFile != null && !imagenFile.isEmpty()) {
                String urlImagen = cloudinaryService.subirImagen(imagenFile);
                planta.setFotoUrl(urlImagen); // Guardamos la URL que nos devuelve Cloudinary
            }

            if (planta.getTipo() != null && planta.getTipo().getId() != null) {
                // Buscamos el tipo real por su ID para que la relación sea sólida
                Tipo tipoSeleccionado = tipoRepo.findById(planta.getTipo().getId()).orElse(null);
                planta.setTipo(tipoSeleccionado);
            }

            repo.save(planta);
        } catch (Exception e) {
            e.printStackTrace(); // Para ver errores en consola si falla la subida
        }
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
