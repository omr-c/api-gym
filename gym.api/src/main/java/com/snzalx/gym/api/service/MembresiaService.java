package com.snzalx.gym.api.service;

import com.snzalx.gym.api.dto.MembresiaDTO;
import com.snzalx.gym.api.dto.RegistroPagoDTO;
import com.snzalx.gym.api.model.Membresia;
import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.repository.MembresiaRepository;
import com.snzalx.gym.api.repository.SocioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
public class MembresiaService {

    private final MembresiaRepository membresiaRepository;
    private final SocioRepository socioRepository;
    private final SocioService socioService;
    private final EmailService emailService;

    public MembresiaService(MembresiaRepository membresiaRepository, 
                            SocioRepository socioRepository, 
                            SocioService socioService,
                            EmailService emailService) {
        this.membresiaRepository = membresiaRepository;
        this.socioRepository = socioRepository;
        this.socioService = socioService;
        this.emailService = emailService;
    }

    @Transactional // Asegurar que toda la operación sea transaccional
    public MembresiaDTO registrarPago(RegistroPagoDTO registroPagoDTO) {
        log.info("Intentando registrar pago para el socio ID: {}", registroPagoDTO.getSocioId());
        
        Socio socio = socioRepository.findById(registroPagoDTO.getSocioId())
                .orElseThrow(() -> new RuntimeException("¡Error! El socio con ID " + registroPagoDTO.getSocioId() + " no existe en la base de datos."));

        // 2. Cambiar estado a activo y guardar el socio
        socio.setEstado("activo");
        socioRepository.save(socio); // Guardar el socio con el estado actualizado

        // 3. Crear membresía
        Membresia membresia = new Membresia();
        membresia.setSocio(socio);
        membresia.setMonto(registroPagoDTO.getMonto());
        membresia.setFechaInicio(LocalDate.now());
        membresia.setFechaVencimiento(LocalDate.now().plusMonths(1));
        membresia.setMetodoPago("Efectivo"); // Valor por defecto según la sugerencia

        Membresia guardada = membresiaRepository.save(membresia);
        
        // 4. Enviar correo (en un try para que no rompa el pago si el mail falla)
        try {
            emailService.enviarConfirmacionPago(socio);
        } catch (Exception e) {
            log.error("El pago se realizó pero el correo falló: {}", e.getMessage());
        }

        return convertToDTO(guardada);
    }

    @Transactional // Asegurar que toda la operación sea transaccional
    public MembresiaDTO activarMembresiaPorPago(UUID socioId, BigDecimal monto) {
        // 1. Buscar al socio por ID
        Socio socio = socioRepository.findById(socioId)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado con ID: " + socioId));

        // --- ESTO ES LO QUE FALTA (según el diagnóstico del frontend) ---
        // 2. Actualizar su estado a 'activo' y guardar el socio
        socio.setEstado("activo");
        socioRepository.save(socio); // Guardamos el cambio en la tabla 'socios'
        // ----------------------------------------------------------------

        // 3. Crear un nuevo registro en la tabla Membresia
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaVencimiento = fechaInicio.plusMonths(1); // 30 días

        Membresia membresia = new Membresia();
        membresia.setSocio(socio);
        membresia.setMonto(monto);
        membresia.setMetodoPago("Efectivo/Tarjeta"); // Valor por defecto
        membresia.setFechaInicio(fechaInicio);
        membresia.setFechaVencimiento(fechaVencimiento);

        Membresia guardada = membresiaRepository.save(membresia);

        // 4. Llamar a emailService.enviarConfirmacionPago(socio)
        if (socio.getEmail() != null && !socio.getEmail().isEmpty()) {
            emailService.enviarConfirmacionPago(socio);
        }

        // 5. Preparar el DTO de respuesta
        return convertToDTO(guardada);
    }

    // Método auxiliar para convertir Membresia a MembresiaDTO
    private MembresiaDTO convertToDTO(Membresia membresia) {
        MembresiaDTO dto = new MembresiaDTO();
        dto.setId(membresia.getId());
        dto.setSocioId(membresia.getSocio().getId());
        dto.setMonto(membresia.getMonto());
        dto.setMetodoPago(membresia.getMetodoPago());
        dto.setFechaInicio(membresia.getFechaInicio());
        dto.setFechaVencimiento(membresia.getFechaVencimiento());
        return dto;
    }
}
