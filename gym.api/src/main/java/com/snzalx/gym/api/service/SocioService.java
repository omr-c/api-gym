package com.snzalx.gym.api.service;

import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.repository.SocioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j // Agregamos SLF4J para logs profesionales
public class SocioService {

    private final SocioRepository socioRepository;
    private final EmailService emailService;

    public SocioService(SocioRepository socioRepository, EmailService emailService) {
        this.socioRepository = socioRepository;
        this.emailService = emailService;
    }

    public Socio registrarSocio(Socio socio) {
        // Log de depuración solicitado por el frontend para ver qué datos están llegando
        log.info("DEBUG: Recibida petición de registro de socio -> {}", socio);
        System.out.println("DEBUG (Consola): Recibida petición de registro de socio -> " + socio);

        // Si el rol no viene especificado, se asigna automáticamente como 'socio'
        if (socio.getRol() == null || socio.getRol().isEmpty()) {
            socio.setRol("socio");
        }

        // Mantenemos el flujo profesional: el socio nace como PENDIENTE de pago
        socio.setEstado("pendiente");
        socio.setQrToken(UUID.randomUUID());
        
        Socio guardado = socioRepository.save(socio);
        
        // Disparo de correo de bienvenida
        if (guardado.getEmail() != null && !guardado.getEmail().isEmpty()) {
            emailService.enviarBienvenida(guardado.getEmail(), guardado.getNombre());
        }
        
        return guardado;
    }

    public Socio obtenerPorQr(UUID qrToken) {
        return socioRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new RuntimeException("socio no encontrado en el sistema"));
    }

    public Socio cambiarEstado(UUID id, String nuevoEstado) {
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("socio no encontrado"));
        socio.setEstado(nuevoEstado);
        return socioRepository.save(socio);
    }

    public List<Socio> listarActivos() {
        return socioRepository.findByEstadoIgnoreCase("activo");
    }
}
