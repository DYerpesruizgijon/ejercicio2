package com.example.ejercicio2.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ejercicio2.Service.EmailService;
import com.example.ejercicio2.model.Usuario;
import com.example.ejercicio2.repository.UsuarioRepository;

@Controller
public class RegistroController {

// 1. Declaramos las herramientas que necesitamos
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // 2. Creamos el constructor para que Spring las "inyecte" automáticamente
    public RegistroController(UsuarioRepository usuarioRepo, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;

    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro() {
        return "registro"; // Debe coincidir con el nombre de tu registro.html
    }

    @PostMapping("/registro")
    public String registrarUsuario(@RequestParam String username, @RequestParam String password, @RequestParam String email, @RequestParam String emailComprobar, @RequestParam String passwordCompro, Model model) {

        if (!password.equals(passwordCompro)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "registro";
        }

        if (password.length() < 6) {
            model.addAttribute("error", "Las contraseña es demasiado corta.");
            return "registro";
        } else if (password.length() > 20) {
            model.addAttribute("error", "Las contraseña es demasiado larga.");
            return "registro";
        }

        // COMPROBACIÓN de email mal escrito
        if (!email.equalsIgnoreCase(emailComprobar)) {
            model.addAttribute("error", "Los correos no coinciden.");
            return "registro";
        }

        // 1. COMPROBACIÓN: usuario
        if (usuarioRepo.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Ese nombre de usuario ya está pillado.");
            return "registro";
        }

        // 2. COMPROBACIÓN: email
        if (usuarioRepo.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Este correo ya tiene una cuenta asociada.");
            return "registro";
        }

        try {
            Usuario nuevo = new Usuario();
            nuevo.setUsername(username);
            nuevo.setEmail(email);
            nuevo.setPassword(passwordEncoder.encode(password));
            nuevo.setRole("ROLE_USER");
            nuevo.setPuntos(0);
            usuarioRepo.save(nuevo);

            // 3. ENVÍO DE EMAIL: Ahora que está guardado, mandamos la bienvenida
            emailService.enviarEmailBienvenida(email, username);

            return "redirect:/login?exito";
        } catch (Exception e) {
            return "redirect:/registro?error"; // Si el nombre ya existe, vuelve con error
        }
    }

}
