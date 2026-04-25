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

    // Se ejecuta todos los días a las 10:00 AM
    @Scheduled(cron = "0 0 10 * * ?") 
    public void enviarRecordatoriosVencimientoDiarios() {
        LocalDate hoy = LocalDate.now();
        LocalDate proximaSemana = hoy.plusDays(7);
        
        // Buscamos membresías que vencen exactamente en 7 días
        List<Membresia> membresiasAVencer = membresiaRepository.findByFechaVencimientoBetween(proximaSemana, proximaSemana);
        
        for (Membresia m : membresiasAVencer) {
            if (m.getSocio().getEmail() != null && !m.getSocio().getEmail().isEmpty()) {
                emailService.enviarRecordatorioVencimiento(
                    m.getSocio().getEmail(), 
                    m.getSocio().getNombre(), 
                    m.getFechaVencimiento().toString()
                );
            }
        }
    }
}
