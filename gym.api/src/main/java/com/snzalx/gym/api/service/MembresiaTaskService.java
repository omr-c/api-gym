package com.snzalx.gym.api.service;

import com.snzalx.gym.api.model.Membresia;
import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.repository.MembresiaRepository;
import com.snzalx.gym.api.repository.SocioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MembresiaTaskService {

    private final MembresiaRepository membresiaRepository;
    private final SocioRepository socioRepository;
    private final EmailService emailService;

    public MembresiaTaskService(MembresiaRepository membresiaRepository, SocioRepository socioRepository, EmailService emailService) {
        this.membresiaRepository = membresiaRepository;
        this.socioRepository = socioRepository;
        this.emailService = emailService;
    }

    // Cron Job Nocturno: Se ejecuta todos los días a las 3:00 AM
    @Scheduled(cron = "0 0 3 * * *")
    public void procesarVencimientosYAlertas() {
        log.info("Iniciando tarea programada de procesamiento de vencimientos y alertas.");
        LocalDate hoy = LocalDate.now();

        // 1. Vencimientos: Buscar socios cuya fecha_vencimiento sea igual o anterior a hoy y cambiar su estado a 'INACTIVO'
        List<Membresia> membresiasVencidasHoyOAntes = membresiaRepository.findByFechaVencimiento(hoy);
        
        for (Membresia membresia : membresiasVencidasHoyOAntes) {
            Socio socio = membresia.getSocio();
            // Solo si el socio está activo, lo cambiamos a inactivo
            if ("activo".equalsIgnoreCase(socio.getEstado())) {
                socio.setEstado("inactivo");
                socioRepository.save(socio);
                log.info("Socio {} (ID: {}) cambiado a estado 'inactivo' por vencimiento.", socio.getNombre(), socio.getId());
            }
        }

        // 2. Alertas Preventivas: Buscar socios que vencen exactamente en 7 días
        LocalDate fechaAlerta = hoy.plusDays(7);
        List<Membresia> membresiasParaAlerta = membresiaRepository.findByFechaVencimiento(fechaAlerta);

        for (Membresia membresia : membresiasParaAlerta) {
            Socio socio = membresia.getSocio();
            if (socio.getEmail() != null && !socio.getEmail().isEmpty()) {
                emailService.enviarRecordatorioVencimiento(
                        socio.getEmail(),
                        socio.getNombre(),
                        membresia.getFechaVencimiento().toString()
                );
                log.info("Enviado recordatorio de vencimiento a {} (ID: {}) para el {}.", socio.getNombre(), socio.getId(), membresia.getFechaVencimiento());
            }
        }
        log.info("Tarea programada de procesamiento de vencimientos y alertas finalizada.");
    }
}
