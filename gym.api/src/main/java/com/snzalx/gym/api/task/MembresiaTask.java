package com.snzalx.gym.api.task;

import com.snzalx.gym.api.model.Membresia;
import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.repository.MembresiaRepository;
import com.snzalx.gym.api.repository.SocioRepository;
import com.snzalx.gym.api.service.EmailService;
import com.snzalx.gym.api.service.SocioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class MembresiaTask {

    private final SocioRepository socioRepository;
    private final MembresiaRepository membresiaRepository;
    private final SocioService socioService;
    private final EmailService emailService;

    public MembresiaTask(SocioRepository socioRepository, MembresiaRepository membresiaRepository, SocioService socioService, EmailService emailService) {
        this.socioRepository = socioRepository;
        this.membresiaRepository = membresiaRepository;
        this.socioService = socioService;
        this.emailService = emailService;
    }

    // Método que se ejecuta diariamente a las 8:00 AM
    @Scheduled(cron = "0 0 8 * * *") // Segundos, Minutos, Horas, Día del mes, Mes, Día de la semana
    public void verificarVencimientosMembresias() {
        log.info("Iniciando tarea programada: Verificación de vencimientos de membresías.");
        LocalDate hoy = LocalDate.now();
        LocalDate fechaRecordatorio = hoy.plusDays(7);

        List<Socio> todosLosSocios = socioRepository.findAll();

        for (Socio socio : todosLosSocios) {
            List<Membresia> membresias = membresiaRepository.findLatestMembresiaBySocioId(socio.getId());

            if (!membresias.isEmpty()) {
                Membresia ultimaMembresia = membresias.get(0);
                LocalDate fechaVencimiento = ultimaMembresia.getFechaVencimiento();

                // Caso 1: Recordatorio de vencimiento (7 días antes)
                if (fechaVencimiento.isEqual(fechaRecordatorio)) {
                    emailService.enviarRecordatorioVencimiento(socio);
                    log.info("Enviado recordatorio de vencimiento a socio {}: su membresía vence el {}", socio.getEmail(), fechaVencimiento);
                }

                // Caso 2: Membresía vencida (hoy o anterior)
                if (fechaVencimiento.isBefore(hoy) || fechaVencimiento.isEqual(hoy)) {
                    if (!"vencido".equalsIgnoreCase(socio.getEstado())) { // Solo cambiar si no está ya vencido
                        socioService.cambiarEstado(socio.getId(), "vencido");
                        emailService.enviarAvisoVencimiento(socio);
                        log.info("Membresía del socio {} ha vencido. Estado cambiado a 'vencido'.", socio.getEmail());
                    }
                }
            }
        }
        log.info("Tarea programada: Verificación de vencimientos de membresías finalizada.");
    }
}
