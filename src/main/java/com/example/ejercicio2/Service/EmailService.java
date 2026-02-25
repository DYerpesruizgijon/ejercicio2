package com.example.ejercicio2.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

    @Value("${BREVO_API_KEY}")
    private String apiKey;

    private final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";
    private final String EMAIL_REMITENTE = "david.yerpes-gordillo@iesruizgijon.com";

    @Async
    public void enviarEmailBienvenida(String destino, String nombreUsuario) {
        String contenido = "Hola " + nombreUsuario + ", gracias por unirte a nuestra comunidad.";
        llamarApiBrevo(destino, nombreUsuario, "¡Bienvenido a la Enciclopedia de Plantas!", contenido);
    }

    @Async
    public void enviarEmailSubidaNivel(String destino, String nombre, String nivel) {
        String contenido = "¡Felicidades " + nombre + "! Has alcanzado el rango de " + nivel;
        llamarApiBrevo(destino, nombre, "¡Subida de Nivel!", contenido);
    }

    private void llamarApiBrevo(String destino, String nombreDestino, String asunto, String contenido) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. Configurar Cabeceras
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        // 2. Construir el JSON manualmente para no fallar
        Map<String, Object> body = new HashMap<>();
        
        Map<String, String> sender = new HashMap<>();
        sender.put("name", "Enciclopedia Plantas");
        sender.put("email", EMAIL_REMITENTE);
        
        Map<String, String> to = new HashMap<>();
        to.put("email", destino);
        to.put("name", nombreDestino);

        body.put("sender", sender);
        body.put("to", Collections.singletonList(to));
        body.put("subject", asunto);
        body.put("textContent", contenido);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, request, String.class);
            if (response.getStatusCode() == HttpStatus.CREATED) {
                System.out.println("✅ Email enviado con éxito por API");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al llamar a Brevo: " + e.getMessage());
        }
    }
}
