package com.snzalx.gym.api.service;

import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.repository.SocioRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class SocioService {

    // inyeccion de dependencias
    private final SocioRepository socioRepository;

    public SocioService(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    // registra un nuevo socio con valores por defecto
    public Socio registrarSocio(Socio socio) {
        // estado inicial por defecto
        socio.setEstado("activo");
        // se genera la identidad digital unica para el qr
        socio.setQrToken(UUID.randomUUID());
        return socioRepository.save(socio);
    }

    // busca al socio usando el token del escaner
    public Socio obtenerPorQr(UUID qrToken) {
        return socioRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new RuntimeException("socio no encontrado en el sistema"));
    }

    // aplica la baja logica o cambio de estado
    public Socio cambiarEstado(UUID id, String nuevoEstado) {
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("socio no encontrado"));
        socio.setEstado(nuevoEstado);
        return socioRepository.save(socio);
    }
}