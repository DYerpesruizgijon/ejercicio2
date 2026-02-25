package com.example.ejercicio2.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.ejercicio2.Service.CloudinaryService;
import com.example.ejercicio2.Service.EmailService;
import com.example.ejercicio2.model.Planta;
import com.example.ejercicio2.model.Tipo;
import com.example.ejercicio2.model.Usuario;
import com.example.ejercicio2.repository.PlantaRepository;
import com.example.ejercicio2.repository.TipoRepository;
import com.example.ejercicio2.repository.UsuarioRepository;

@Controller
public class PlantaController {

    private final PlantaRepository repo;
    private final TipoRepository tipoRepo;
    private final CloudinaryService cloudinaryService;
    private final UsuarioRepository usuarioRepo;
    private final EmailService emailService;

    public PlantaController(PlantaRepository repo, TipoRepository tipoRepo, CloudinaryService cloudinaryService, UsuarioRepository usuarioRepo, EmailService emailService) {
        this.repo = repo;
        this.tipoRepo = tipoRepo;
        this.cloudinaryService = cloudinaryService;
        this.usuarioRepo = usuarioRepo;
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        // 1. Enviamos siempre la lista de plantas
        model.addAttribute("plantas", repo.findAllByOrderByIdAsc());

        /*logica pa la tabla de ranking */
        List<Usuario> ranking = usuarioRepo.findAll();
        ranking.sort((u1, u2) -> Integer.compare(u2.getPuntos(), u1.getPuntos())); // Ordenar por puntos de mayor a menor
        model.addAttribute("ranking", ranking);

        if (authentication != null && authentication.isAuthenticated()) {
            // 2. BUSCAMOS AL USUARIO LOGUEADO para que el HTML tenga sus datos
            Usuario user = usuarioRepo.findByUsername(authentication.getName()).orElse(null);
            model.addAttribute("usuarioActual", user); // <--- ESTO ES VITAL

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                return "index-admin";
            }
        }

        return "index"; // Aquí se caía si index.html pedía datos del usuario que no enviamos
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
    public String addPlanta(Planta planta, @RequestParam("imagenFile") MultipartFile imagenFile, Authentication authentication) {
        try {
            // 1. Asignar el nombre del usuario logueado
            if (authentication != null && authentication.isAuthenticated()) {
                // Obtenemos el username del usuario que tiene la sesión iniciada
                planta.setCreador(authentication.getName());
            }
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

            /*LOGICA PARA SUMAR PUNTOS Y MANDAR CORREO */
            // Buscamos al usuario para actualizar sus puntos
            Usuario autor = usuarioRepo.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Sumamos 10 puntos (esto disparará el cambio de rango automáticamente al consultar getRango)
            String rangoAnterior = autor.getRango();

            autor.setPuntos(autor.getPuntos() + 10);

            // 3. Sumamos los puntos y guardamos
            usuarioRepo.save(autor);

            // 4. OBTENEMOS EL NUEVO RANGO (Después de la suma)
            String rangoNuevo = autor.getRango();

            // 5. COMPROBACIÓN: Si el rango ha cambiado, mandamos el email
            if (!rangoAnterior.equals(rangoNuevo)) {
                emailService.enviarEmailSubidaNivel(autor.getEmail(), autor.getUsername(), rangoNuevo);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Para ver errores en consola si falla la subida
        }
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Planta planta = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Planta no encontrada con ID: " + id));

        model.addAttribute("planta", planta);
        model.addAttribute("todosLosTipos", tipoRepo.findAll());
        return "edit";
    }

    @PostMapping("/edit/{id}")
    public String editPlanta(@PathVariable Long id,
            @ModelAttribute Planta plantaForm,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile) {

        // 1. Buscamos la planta real en la DB para no perder datos importantes
        Planta plantaBD = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Planta no encontrada"));

        // 2. Actualizamos los datos que vienen del formulario
        plantaBD.setNombre(plantaForm.getNombre());
        plantaBD.setAltura(plantaForm.getAltura());
        plantaBD.setUbicacion(plantaForm.getUbicacion());
        plantaBD.setRareza(plantaForm.getRareza());
        plantaBD.setNotasCampo(plantaForm.getNotasCampo());

        // 3. Lógica del Tipo (usando tu lógica de buscar por nombre)
        if (plantaForm.getTipo() != null && plantaForm.getTipo().getId() != null) {
            Tipo tipoSeleccionado = tipoRepo.findById(plantaForm.getTipo().getId())
                    .orElseThrow(() -> new RuntimeException("Tipo no encontrado"));
            plantaBD.setTipo(tipoSeleccionado);
        }

        // 4. NUEVA FOTO: Solo si el usuario ha subido un archivo nuevo
        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String urlNueva = cloudinaryService.subirImagen(imagenFile);
                plantaBD.setFotoUrl(urlNueva);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Si no sube nada, plantaBD mantiene la fotoUrl que ya tenía.

        repo.save(plantaBD);
        return "redirect:/perfil"; // Mejor redirigir al perfil para ver el cambio
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

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // Buscamos los datos del usuario logueado para ver sus puntos y rango
            Usuario usuario = usuarioRepo.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Buscamos las plantas donde el creador sea el usuario actual
            List<Planta> misPlantas = repo.findByCreador(authentication.getName());

            model.addAttribute("usuario", usuario);
            model.addAttribute("registros", misPlantas);

            return "perfil"; // Esto cargará el archivo perfil.html
        }
        return "redirect:/login";
    }

    @GetMapping("/planta/detalle/{id}")
    public String mostrarDetalles(@PathVariable("id") Long id, Model model) {
        // Buscamos la planta por su ID
        Planta planta = repo.findById(id).orElse(null);

        if (planta == null) {
            return "redirect:/"; // Si no existe, al index
        }

        model.addAttribute("planta", planta);
        return "detalles"; // El nombre del archivo HTML
    }

}
