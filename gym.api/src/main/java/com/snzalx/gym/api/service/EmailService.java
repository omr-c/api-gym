package com.snzalx.gym.api.service;

import com.snzalx.gym.api.model.Membresia;
import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.repository.MembresiaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException; // Importar MailException
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final MembresiaRepository membresiaRepository;

    public EmailService(JavaMailSender mailSender, MembresiaRepository membresiaRepository) {
        this.mailSender = mailSender;
        this.membresiaRepository = membresiaRepository;
    }

    // 1. Correo de Bienvenida (cuando se registra el socio, estado 'pendiente')
    @Async
    public void enviarCorreoBienvenida(Socio socio) {
        log.info("Enviando correo de bienvenida a {}", socio.getEmail());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(socio.getEmail());
        message.setSubject("¡Bienvenido a Gym Rats! Tu cuenta está pendiente de activación");
        message.setText("Hola " + socio.getNombre() + ",\n\n" +
                "¡Gracias por unirte a nuestra comunidad! Tu cuenta ha sido creada exitosamente.\n\n" +
                "Para activar tu membresía y comenzar a entrenar, por favor, pasa por recepción para realizar tu primer pago.\n\n" +
                "¡Te esperamos!\n" +
                "Equipo de Gym Rats.");
        try {
            mailSender.send(message);
            log.info("Correo de bienvenida enviado exitosamente a {}", socio.getEmail());
        } catch (MailException e) {
            log.error("Error al enviar correo de bienvenida a {}: {}", socio.getEmail(), e.getMessage());
            // Aquí podrías añadir lógica para reintentar o registrar el fallo en una base de datos
        }
    }

    // 2. Correo de Confirmación de Pago (cuando el admin registra el pago, cambia estado a 'activo')
    @Async
    public void enviarConfirmacionPago(Socio socio) {
        log.info("Enviando confirmación de pago a {}", socio.getEmail());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(socio.getEmail());
        message.setSubject("¡Pago Confirmado! Tu Membresía Gym Rats está activa");

        List<Membresia> membresias = membresiaRepository.findLatestMembresiaBySocioId(socio.getId());
        String fechaVencimiento = "N/A";
        if (!membresias.isEmpty()) {
            fechaVencimiento = membresias.get(0).getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        message.setText("Hola " + socio.getNombre() + ",\n\n" +
                "¡Tu pago ha sido registrado exitosamente! Tu membresía en Gym Rats ya está activa.\n\n" +
                "Tu membresía es válida hasta el: " + fechaVencimiento + ".\n\n" +
                "¡Disfruta de tus entrenamientos!\n" +
                "Equipo de Gym Rats.");
        try {
            mailSender.send(message);
            log.info("Correo de confirmación de pago enviado exitosamente a {}", socio.getEmail());
        } catch (MailException e) {
            log.error("Error al enviar correo de confirmación de pago a {}: {}", socio.getEmail(), e.getMessage());
        }
    }

    // 3. Correo de Recordatorio de Vencimiento (7 días antes de expirar)
    @Async
    public void enviarRecordatorioVencimiento(Socio socio) {
        log.info("Enviando recordatorio de vencimiento a {}", socio.getEmail());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(socio.getEmail());
        message.setSubject("Recordatorio Importante: Tu Membresía Gym Rats está por vencer");

        List<Membresia> membresias = membresiaRepository.findLatestMembresiaBySocioId(socio.getId());
        String fechaVencimiento = "N/A";
        if (!membresias.isEmpty()) {
            fechaVencimiento = membresias.get(0).getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        message.setText("Hola " + socio.getNombre() + ",\n\n" +
                "Te recordamos que tu membresía de Gym Rats vencerá pronto, el " + fechaVencimiento + ".\n\n" +
                "¡No dejes que tu entrenamiento se detenga! Renueva tu membresía con anticipación para seguir disfrutando de todos nuestros servicios.\n\n" +
                "Equipo de Gym Rats.");
        try {
            mailSender.send(message);
            log.info("Correo de recordatorio de vencimiento enviado exitosamente a {}", socio.getEmail());
        } catch (MailException e) {
            log.error("Error al enviar correo de recordatorio de vencimiento a {}: {}", socio.getEmail(), e.getMessage());
        }
    }

    // 4. Correo de Aviso de Vencimiento (cuando el acceso ha sido restringido)
    @Async
    public void enviarAvisoVencimiento(Socio socio) {
        log.info("Enviando aviso de vencimiento y restricción de acceso a {}", socio.getEmail());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(socio.getEmail());
        message.setSubject("Aviso Importante: Acceso Restringido - Membresía Gym Rats Vencida");

        message.setText("Hola " + socio.getNombre() + ",\n\n" +
                "Te informamos que tu membresía de Gym Rats ha vencido y tu acceso a nuestras instalaciones ha sido restringido.\n\n" +
                "Para reactivar tu membresía y continuar con tus entrenamientos, por favor, pasa por recepción.\n\n" +
                "¡Esperamos verte pronto!\n" +
                "Equipo de Gym Rats.");
        try {
            mailSender.send(message);
            log.info("Correo de aviso de vencimiento enviado exitosamente a {}", socio.getEmail());
        } catch (MailException e) {
            log.error("Error al enviar correo de aviso de vencimiento a {}: {}", socio.getEmail(), e.getMessage());
        }
    }
}
