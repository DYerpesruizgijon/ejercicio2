package com.example.ejercicio2.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailSubidaNivel(String destino, String nombre, String nivel) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destino);
        mensaje.setSubject("¡Subida de Nivel en tu Enciclopedia!");
        mensaje.setText("¡Felicidades " + nombre + "! Has alcanzado el rango de " + nivel);
        mailSender.send(mensaje);
    }
}