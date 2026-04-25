package com.snzalx.gym.api.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void enviarReciboPago(String destinatario, String nombreSocio, Double monto, String fechaVencimiento) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Recibo de Pago - Gym Rats");
        message.setText("Hola " + nombreSocio + ",\n\n" +
                "Hemos registrado tu pago exitosamente.\n\n" +
                "Detalles del pago:\n" +
                "- Monto: $" + monto + "\n" +
                "- Tu membresía es válida hasta: " + fechaVencimiento + "\n\n" +
                "¡Gracias por entrenar con nosotros!\n" +
                "Equipo de Gym Rats.");
        
        mailSender.send(message);
    }

    @Async
    public void enviarBienvenida(String destinatario, String nombreSocio) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("¡Bienvenido a Gym Rats!");
        message.setText("Hola " + nombreSocio + ",\n\n" +
                "¡Gracias por unirte a nuestra comunidad! Tu registro se ha realizado con éxito.\n\n" +
                "Tu estado actual es PENDIENTE. Para poder acceder al gimnasio con tu código QR, por favor acude a recepción para realizar tu primer pago.\n\n" +
                "¡Estamos listos para ayudarte a cumplir tus metas!\n\n" +
                "Equipo de Gym Rats.");
        
        mailSender.send(message);
    }

    @Async
    public void enviarRecordatorioVencimiento(String destinatario, String nombreSocio, String fechaVencimiento) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Recordatorio de Vencimiento de Membresía - Gym Rats");
        message.setText("Hola " + nombreSocio + ",\n\n" +
                "Te escribimos para recordarte que tu membresía está próxima a vencer.\n\n" +
                "Fecha de vencimiento: " + fechaVencimiento + "\n\n" +
                "Te invitamos a realizar tu renovación a tiempo para que puedas seguir disfrutando de nuestras instalaciones sin interrupciones.\n\n" +
                "¡Nos vemos en el entrenamiento!\n" +
                "Equipo de Gym Rats.");
        
        mailSender.send(message);
    }
}
