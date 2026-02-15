package com.example.ejercicio2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.ejercicio2.model.Usuario;
import com.example.ejercicio2.repository.UsuarioRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public DataSeeder(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repo.count() == 0) { // Solo si la tabla está vacía
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            // Usamos el codificador que definiste con @Bean
            admin.setPassword(encoder.encode("1234")); 
            admin.setRole("ROLE_ADMIN");
            repo.save(admin);
            System.out.println("Usuario inicial creado: admin / 1234");
        }
    }
}