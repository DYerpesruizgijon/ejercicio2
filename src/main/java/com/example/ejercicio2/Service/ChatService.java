package com.example.ejercicio2.Service;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatService {

    // Cambiamos la propiedad para que lea la de huggingface
    @Value("${huggingface.api.key}")
    private String apiKey;

    // Nueva URL para el modelo Mistral de Hugging Face
    private final String URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.3";

    public String obtenerRecomendaciones(String ubicacion) {
        RestTemplate restTemplate = new RestTemplate();

        // El formato de Hugging Face es más directo ("inputs")
        String prompt = "<s>[INST] Eres un experto botánico. Dime 3 plantas nativas que puedo encontrar en " + ubicacion + ". Da una descripción de una frase para cada una. Responde solo en español. [/INST]";

        Map<String, Object> body = Map.of(
            "inputs", prompt,
            "parameters", Map.of("max_new_tokens", 500)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            // Hugging Face devuelve una lista de mapas
            ResponseEntity<List> response = restTemplate.postForEntity(URL, entity, List.class);
            Map<String, Object> res = (Map<String, Object>) response.getBody().get(0);
            String texto = (String) res.get("generated_text");
            
            // Limpiamos la respuesta para que no repita tu pregunta original
            if (texto.contains("[/INST]")) {
                return texto.split("\\[/INST\\]")[1].trim();
            }
            return texto;
        } catch (Exception e) {
            // Imprime el error en la consola por si falla algo
            e.printStackTrace();
            return "La IA está arrancando. Por favor, espera 10 segundos y vuelve a consultar.";
        }
    }
}