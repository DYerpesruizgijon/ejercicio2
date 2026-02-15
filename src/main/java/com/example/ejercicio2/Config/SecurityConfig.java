package com.example.ejercicio2.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/css/**", "/login").permitAll() // ¡Permite ver el login!
                .requestMatchers("/add", "/edit/**", "/delete/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                )
                .formLogin(login -> login
                .loginPage("/login") // Tu ruta personalizada
                .defaultSuccessUrl("/", true) // A dónde ir al entrar
                .permitAll()
                )
                .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
