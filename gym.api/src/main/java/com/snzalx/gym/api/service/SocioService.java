package com.snzalx.gym.api.service;

import com.snzalx.gym.api.dto.SocioDTO;
import com.snzalx.gym.api.model.Membresia;
import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.repository.MembresiaRepository;
import com.snzalx.gym.api.repository.SocioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SocioService {

    private final SocioRepository socioRepository;
    private final MembresiaRepository membresiaRepository;
    private final EmailService emailService;

    public SocioService(SocioRepository socioRepository, MembresiaRepository membresiaRepository, EmailService emailService) {
        this.socioRepository = socioRepository;
        this.membresiaRepository = membresiaRepository;
        this.emailService = emailService;
    }

    public SocioDTO registrarSocio(Socio socio) {
        log.info("Intentando registrar socio con email: {}", socio.getEmail());

        if (socioRepository.findByEmail(socio.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // --- CORRECCIÓN CRÍTICA AQUÍ ---
        // Generamos el token único para el QR antes de guardar en la BD
        if (socio.getQrToken() == null) {
            socio.setQrToken(UUID.randomUUID());
            log.info("Generando nuevo QR Token para el socio: {}", socio.getQrToken());
        }
        // -------------------------------

        if (socio.getEstado() == null) {
            socio.setEstado("vencido");
        }

        Socio guardado = socioRepository.save(socio);

        try {
            emailService.enviarCorreoBienvenida(guardado);
        } catch (Exception e) {
            log.error("Error al enviar correo de bienvenida: {}", e.getMessage());
        }

        return convertToDTO(guardado);
    }

    public List<SocioDTO> findAllSocios() {
        return socioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public long listarActivos() {
        return socioRepository.countByEstadoIgnoreCase("activo");
    }

    public long countSociosWithExpiredMembership() {
        return socioRepository.countByEstadoIgnoreCase("vencido");
    }

    public void cambiarEstado(UUID id, String nuevoEstado) {
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado"));
        socio.setEstado(nuevoEstado);
        socioRepository.save(socio);
        log.info("Estado del socio {} actualizado a {}", id, nuevoEstado);
    }

    public SocioDTO obtenerSocioDtoPorQr(UUID qrToken) {
        Socio socio = socioRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado"));
        return convertToDTO(socio);
    }

    public SocioDTO convertToDTO(Socio socio) {
        SocioDTO dto = new SocioDTO();
        dto.setId(socio.getId());
        dto.setNombre(socio.getNombre());
        dto.setTelefono(socio.getTelefono());
        dto.setEmail(socio.getEmail());
        dto.setFotoUrl(socio.getFotoUrl());
        dto.setQrToken(socio.getQrToken()); // Aquí ahora viajará el UUID real
        dto.setBio(socio.getBio());
        dto.setInstagramUrl(socio.getInstagramUrl());
        dto.setEstado(socio.getEstado());
        dto.setRol(socio.getRol());

        List<Membresia> membresias = membresiaRepository.findLatestMembresiaBySocioId(socio.getId());
        if (!membresias.isEmpty()) {
            Membresia ultima = membresias.get(0);
            LocalDate hoy = LocalDate.now();
            if (ultima.getFechaVencimiento().isAfter(hoy)) {
                dto.setDiasRestantes(ChronoUnit.DAYS.between(hoy, ultima.getFechaVencimiento()));
            } else {
                dto.setDiasRestantes(0L);
            }
        } else {
            dto.setDiasRestantes(0L);
        }
        return dto;
    }
}