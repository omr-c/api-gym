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

    public Socio registrarSocio(Socio socio) {
        log.info("DEBUG: Recibida petición de registro de socio -> {}", socio);
        System.out.println("DEBUG (Consola): Recibida petición de registro de socio -> " + socio);

        if (socio.getRol() == null || socio.getRol().isEmpty()) {
            socio.setRol("socio");
        }

        socio.setEstado("pendiente");
        socio.setQrToken(UUID.randomUUID());
        
        Socio guardado = socioRepository.save(socio);
        
        if (guardado.getEmail() != null && !guardado.getEmail().isEmpty()) {
            emailService.enviarBienvenida(guardado.getEmail(), guardado.getNombre());
        }
        
        return guardado;
    }

    // Este método es llamado por AccesoService y espera un objeto Socio
    public Socio obtenerPorQr(UUID qrToken) {
        return socioRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new RuntimeException("socio no encontrado en el sistema"));
    }

    // Nuevo método para que el frontend obtenga el SocioDTO con diasRestantes
    public SocioDTO obtenerSocioDtoPorQr(UUID qrToken) {
        Socio socio = socioRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new RuntimeException("socio no encontrado en el sistema"));
        return convertSocioToDto(socio);
    }

    public Socio cambiarEstado(UUID id, String nuevoEstado) {
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("socio no encontrado"));
        socio.setEstado(nuevoEstado);
        return socioRepository.save(socio);
    }

    public List<SocioDTO> listarActivos() {
        List<Socio> sociosActivos = socioRepository.findByEstadoIgnoreCase("activo");
        return sociosActivos.stream()
                .map(this::convertSocioToDto)
                .collect(Collectors.toList());
    }

    // Método auxiliar para convertir Socio a SocioDTO y calcular diasRestantes
    private SocioDTO convertSocioToDto(Socio socio) {
        SocioDTO dto = new SocioDTO();
        dto.setId(socio.getId());
        dto.setNombre(socio.getNombre());
        dto.setTelefono(socio.getTelefono());
        dto.setEmail(socio.getEmail());
        dto.setFotoUrl(socio.getFotoUrl());
        dto.setQrToken(socio.getQrToken());
        dto.setBio(socio.getBio());
        dto.setInstagramUrl(socio.getInstagramUrl());
        dto.setEstado(socio.getEstado());

        // Calcular diasRestantes
        List<Membresia> membresias = membresiaRepository.findLatestMembresiaBySocioId(socio.getId());
        if (!membresias.isEmpty()) {
            Membresia ultimaMembresia = membresias.get(0); // La más reciente por el ORDER BY
            LocalDate hoy = LocalDate.now();
            if (ultimaMembresia.getFechaVencimiento().isAfter(hoy)) {
                dto.setDiasRestantes(ChronoUnit.DAYS.between(hoy, ultimaMembresia.getFechaVencimiento()));
            } else {
                dto.setDiasRestantes(0L); // Membresía vencida o vence hoy
            }
        } else {
            dto.setDiasRestantes(0L); // No tiene membresías
        }

        return dto;
    }
}
