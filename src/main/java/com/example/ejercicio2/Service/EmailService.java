package com.example.ejercicio2.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        // --- DISEÑO HTML ---
        String htmlLayout
                = "<html><body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>"
                + "  <div style='max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 10px; border: 1px solid #ddd;'>"
                + "    <h2 style='color: #2e7d32; text-align: center;'>🌿 Enciclopedia de Plantas</h2>"
                + "    <hr style='border: 0; border-top: 1px solid #eee;'>"
                + "    <p style='font-size: 16px; color: #333;'>Hola <strong>" + nombreDestino + "</strong>,</p>"
                + "    <p style='font-size: 16px; color: #555; line-height: 1.5;'>" + contenido + "</p>"
                + "    <div style='text-align: center; margin-top: 30px;'>"
                + "      <a href='https://ejercicio2-4uo5.onrender.com' style='background-color: #2e7d32; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px;'>Visitar mi Jardín</a>"
                + "    </div>"
                + "    <p style='margin-top: 40px; font-size: 12px; color: #999; text-align: center;'>Este es un mensaje automático, por favor no respondas.</p>"
                + "  </div>"
                + "</body></html>";

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
        // Cambiamos textContent por htmlContent
        body.put("htmlContent", htmlLayout);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(BREVO_API_URL, request, String.class);
        } catch (Exception e) {
            System.err.println(" Error Brevo HTML: " + e.getMessage());
        }
    }
}
