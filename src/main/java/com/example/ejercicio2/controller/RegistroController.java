package com.example.ejercicio2.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ejercicio2.model.Usuario;
import com.example.ejercicio2.repository.UsuarioRepository;

@Controller
public class RegistroController {
    
// 1. Declaramos las herramientas que necesitamos
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    // 2. Creamos el constructor para que Spring las "inyecte" automáticamente
    public RegistroController(UsuarioRepository usuarioRepo, PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
    }

@PostMapping("/registro")
public String registrarUsuario(@RequestParam String username, @RequestParam String password) {
    try {
        Usuario nuevo = new Usuario();
        nuevo.setUsername(username);
        nuevo.setPassword(passwordEncoder.encode(password));
        nuevo.setRole("ROLE_USER");
        nuevo.setPuntos(0);
        usuarioRepo.save(nuevo);
        return "redirect:/login?exito";
    } catch (Exception e) {
        return "redirect:/registro?error"; // Si el nombre ya existe, vuelve con error
    }
}

}
