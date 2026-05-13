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

    // envia un correo con asunto y cuerpo personalizado para codigos u otros avisos
    @Async
    public void enviarCorreoGenerico(String destinatario, String asunto, String mensaje) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject(asunto);
        message.setText(mensaje);
        mailSender.send(message);
    }

    // envia el recibo cuando se registra un pago
    @Async
    public void enviarReciboPago(String destinatario, String nombreSocio, Double monto, String fechaVencimiento) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Recibo de Pago - Gym Rats");
        message.setText("Hola " + nombreSocio + ",\n\n" +
                "Hemos registrado tu pago exitosamente.\n\n" +
                "Detalles del pago:\n" +
                "- Monto: $" + monto + "\n" +
                "- Tu membresia es valida hasta: " + fechaVencimiento + "\n\n" +
                "¡Gracias por entrenar con nosotros!\n" +
                "Equipo de Gym Rats.");

        mailSender.send(message);
    }

    // envia correo automatico al crear la cuenta
    @Async
    public void enviarBienvenida(String destinatario, String nombreSocio) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("¡Bienvenido a Gym Rats!");
        message.setText("Hola " + nombreSocio + ",\n\n" +
                "¡Gracias por unirte a nuestra comunidad! Tu registro se ha realizado con exito.\n\n" +
                "Tu estado actual es pendiente. Acude a recepcion para activar tu membresia.\n\n" +
                "Equipo de Gym Rats.");

        mailSender.send(message);
    }

    // avisa al socio que su membresia esta por vencer
    @Async
    public void enviarRecordatorioVencimiento(String destinatario, String nombreSocio, String fechaVencimiento) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Recordatorio de Vencimiento de Membresia - Gym Rats");
        message.setText("Hola " + nombreSocio + ",\n\n" +
                "Te escribimos para recordarte que tu membresia esta proxima a vencer.\n\n" +
                "Fecha de vencimiento: " + fechaVencimiento + "\n\n" +
                "Te invitamos a renovar a tiempo para evitar interrupciones.\n\n" +
                "Equipo de Gym Rats.");

        mailSender.send(message);
    }
}