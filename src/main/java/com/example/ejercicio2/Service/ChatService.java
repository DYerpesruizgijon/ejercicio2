package com.example.ejercicio2.Service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory; // IMPORTANTE
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatService {

    @Value("${huggingface.api.key}")
    private String apiKey;

    private final String URL = "https://router.huggingface.co/v1/chat/completions";

    public String obtenerRecomendaciones(String ubicacion) {
        // Configuramos el cliente para que tenga PACIENCIA (60 segundos)
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000);
        factory.setReadTimeout(60000);

        RestTemplate restTemplate = new RestTemplate(factory);

        String prompt = "<s>[INST] Eres un experto botánico. Dime 3 plantas nativas que puedo encontrar en " + ubicacion + ". Da una descripción de una frase para cada una. Responde solo en español. [/INST]";

        Map<String, Object> body = Map.of(
                "model", "meta-llama/Llama-3.1-8B-Instruct", // El modelo que te ha dado el 200 OK
                "messages", List.of(
                        Map.of("role", "user", "content", "Eres un experto botánico. Dime 3 plantas nativas de " + ubicacion + " con una descripción corta en español.")
                ),
                "max_tokens", 250
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(URL, entity, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content"); // Aquí ya tienes el texto de las plantas
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
