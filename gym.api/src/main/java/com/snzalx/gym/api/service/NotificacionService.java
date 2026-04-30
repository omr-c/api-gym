package com.snzalx.gym.api.service;

import com.snzalx.gym.api.model.Membresia;
import com.snzalx.gym.api.repository.MembresiaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class NotificacionService {

    private final MembresiaRepository membresiaRepository;
    private final EmailService emailService;

    public NotificacionService(MembresiaRepository membresiaRepository, EmailService emailService) {
        this.membresiaRepository = membresiaRepository;
        this.emailService = emailService;
    }

    // se ejecuta todos los dias a las 10:00 am para buscar vencimientos[cite: 20]
    @Scheduled(cron = "0 0 10 * * ?")
    public void enviarRecordatoriosVencimientoDiarios() {
        LocalDate hoy = LocalDate.now();
        LocalDate proximaSemana = hoy.plusDays(7);

        // buscamos membresias que vencen exactamente en 7 dias[cite: 20]
        List<Membresia> membresiasAVencer = membresiaRepository.findByFechaVencimientoBetween(proximaSemana, proximaSemana);

        for (Membresia m : membresiasAVencer) {
            // verificamos que el socio tenga un correo valido antes de enviar[cite: 20]
            if (m.getSocio().getEmail() != null && !m.getSocio().getEmail().isEmpty()) {
                // llamada al metodo de emailservice con los 3 parametros de tipo string[cite: 18, 20]
                emailService.enviarRecordatorioVencimiento(
                        m.getSocio().getEmail(),
                        m.getSocio().getNombre(),
                        m.getFechaVencimiento().toString()
                );
            }
        }
    }
}