package com.example.ejercicio2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ejercicio2.Service.ChatService;

@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // Coincide con el th:action del formulario de tu HTML
    @GetMapping("/registro-planta/asistente")
    public String asistente(@RequestParam(name = "ubicacion", required = false) String ubicacion, Model model) {
        
        if (ubicacion != null && !ubicacion.isEmpty()) {
            // Llamamos al servicio que conecta con Hugging Face
            String respuestaIA = chatService.obtenerRecomendaciones(ubicacion);
            
            // Pasamos los datos al HTML
            model.addAttribute("respuesta", respuestaIA);
            model.addAttribute("ubicacionAnterior", ubicacion);
        }
        
        return "asistente"; // Nombre de tu archivo HTML (asistente.html)
    }
}