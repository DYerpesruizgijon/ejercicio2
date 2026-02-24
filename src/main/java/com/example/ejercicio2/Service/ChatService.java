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

    private final String URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.3";

    public String obtenerRecomendaciones(String ubicacion) {
        // Configuramos el cliente para que tenga PACIENCIA (60 segundos)
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000); 
        factory.setReadTimeout(60000);    
        
        RestTemplate restTemplate = new RestTemplate(factory);

        String prompt = "<s>[INST] Eres un experto botánico. Dime 3 plantas nativas que puedo encontrar en " + ubicacion + ". Da una descripción de una frase para cada una. Responde solo en español. [/INST]";

        Map<String, Object> body = Map.of(
                "inputs", prompt,
                "parameters", Map.of(
                        "max_new_tokens", 500,
                        "wait_for_model", true 
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            // Intentamos obtener la respuesta
            ResponseEntity<List> response = restTemplate.postForEntity(URL, entity, List.class);
            
            if (response.getBody() != null && !response.getBody().isEmpty()) {
                Map<String, Object> res = (Map<String, Object>) response.getBody().get(0);
                String texto = (String) res.get("generated_text");

                if (texto.contains("[/INST]")) {
                    return texto.split("\\[/INST\\]")[1].trim();
                }
                return texto;
            }
            return "La IA devolvió una respuesta vacía.";
            
        } catch (Exception e) {
            // Imprime el error real para que lo veamos en el log
            System.err.println("--- ERROR DETECTADO ---");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            
            return "Error al conectar con la IA: " + e.getMessage();
        }
    }
}