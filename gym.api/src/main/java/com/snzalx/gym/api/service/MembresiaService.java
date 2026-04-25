package com.snzalx.gym.api.service;

import com.snzalx.gym.api.dto.MembresiaDTO;
import com.snzalx.gym.api.model.Membresia;
import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.repository.MembresiaRepository;
import com.snzalx.gym.api.repository.SocioRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class MembresiaService {

    private final MembresiaRepository membresiaRepository;
    private final SocioRepository socioRepository;
    private final SocioService socioService;

    public MembresiaService(MembresiaRepository membresiaRepository, 
                            SocioRepository socioRepository, 
                            SocioService socioService) {
        this.membresiaRepository = membresiaRepository;
        this.socioRepository = socioRepository;
        this.socioService = socioService;
    }

    // Lógica principal para registrar un pago y renovar la membresía
    public MembresiaDTO registrarPago(MembresiaDTO dto) {
        // 1. Buscamos al socio para vincular el pago
        Socio socio = socioRepository.findById(dto.getSocioId())
                .orElseThrow(() -> new RuntimeException("Socio no encontrado para registrar pago"));

        // 2. Definimos la fecha de inicio (hoy)
        LocalDate fechaInicio = LocalDate.now();
        
        // 3. Calculamos la fecha de vencimiento según la duración recibida (por defecto 30 días)
        int dias = (dto.getDuracionDias() != null) ? dto.getDuracionDias() : 30;
        LocalDate fechaVencimiento = fechaInicio.plusDays(dias);

        // 4. Creamos y guardamos la entidad Membresia
        Membresia membresia = new Membresia();
        membresia.setSocio(socio);
        membresia.setMonto(dto.getMonto());
        membresia.setMetodoPago(dto.getMetodoPago());
        membresia.setFechaInicio(fechaInicio);
        membresia.setFechaVencimiento(fechaVencimiento);

        Membresia guardada = membresiaRepository.save(membresia);

        // 5. Actualizamos el estado del socio a "activo" usando el método de Omar
        socioService.cambiarEstado(socio.getId(), "activo");

        // 6. Preparamos el DTO de respuesta para la App Flutter
        MembresiaDTO respuesta = new MembresiaDTO();
        respuesta.setId(guardada.getId());
        respuesta.setSocioId(socio.getId());
        respuesta.setMonto(guardada.getMonto());
        respuesta.setMetodoPago(guardada.getMetodoPago());
        respuesta.setFechaInicio(guardada.getFechaInicio());
        respuesta.setFechaVencimiento(guardada.getFechaVencimiento());
        respuesta.setDuracionDias(dias);

        return respuesta;
    }
}
