package com.example.ejercicio2.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // private final JavaMailSender mailSender;

    // public EmailService( JavaMailSender mailSender){
    //     this.mailSender=mailSender;
    // }

    @Async
    public void enviarEmailBienvenida(String destino, String nombreUsuario) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destino);
        mensaje.setFrom("david.yerpes-gordillo@iesruizgijon.com");
        mensaje.setSubject("¡Bienvenido a la Enciclopedia de Plantas!");
        mensaje.setText("Hola " + nombreUsuario + ",\n\n"
                + "Gracias por unirte a nuestra comunidad. "
                + "¡Empieza a subir tus plantas para ganar puntos y subir de nivel!");
        mailSender.send(mensaje);
    }

    public void enviarEmailSubidaNivel(String destino, String nombre, String nivel) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destino);
        mensaje.setFrom("david.yerpes-gordillo@iesruizgijon.com");
        mensaje.setSubject("¡Subida de Nivel en tu Enciclopedia!");
        mensaje.setText("¡Felicidades " + nombre + "! Has alcanzado el rango de " + nivel);
        mailSender.send(mensaje);
    }
}
