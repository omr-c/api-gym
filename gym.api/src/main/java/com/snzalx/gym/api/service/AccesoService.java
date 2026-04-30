package com.snzalx.gym.api.service;

import com.snzalx.gym.api.dto.AccesoDTO;
import com.snzalx.gym.api.model.Acceso;
import com.snzalx.gym.api.model.Membresia;
import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.repository.AccesoRepository;
import com.snzalx.gym.api.repository.MembresiaRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccesoService {

    private final AccesoRepository accesoRepository;
    private final SocioService socioService;
    private final MembresiaRepository membresiaRepository;

    public AccesoService(AccesoRepository accesoRepository,
                         SocioService socioService,
                         MembresiaRepository membresiaRepository) {
        this.accesoRepository = accesoRepository;
        this.socioService = socioService;
        this.membresiaRepository = membresiaRepository;
    }

    public AccesoDTO registrarAccesoEscaner(UUID qrToken) {
        Socio socio = socioService.obtenerPorQr(qrToken);

        Acceso acceso = new Acceso();
        acceso.setSocio(socio);
        acceso.setFechaHora(LocalDateTime.now());

        AccesoDTO respuesta = new AccesoDTO();
        respuesta.setNombreSocio(socio.getNombre());
        respuesta.setFechaHora(acceso.getFechaHora());
        respuesta.setSocioId(socio.getId()); // Aseguramos que el socioId se envíe al frontend

        // agregamos el id del socio para que flutter pueda registrar el pago
        respuesta.setSocioId(socio.getId());

        // buscamos el historial de membresias del socio
        List<Membresia> membresias = membresiaRepository.findBySocioId(socio.getId());

        LocalDate hoy = LocalDate.now();
        boolean tieneAcceso = false;

        for (Membresia mem : membresias) {
            if (!mem.getFechaVencimiento().isBefore(hoy)) {
                tieneAcceso = true;
                break;
            }
        }

        if (tieneAcceso) {
            acceso.setEsExitoso(true);
            respuesta.setEsExitoso(true);
            respuesta.setMensajeSemaforo("luz verde: acceso permitido, pago vigente");

            if (!"activo".equals(socio.getEstado())) {
                socioService.cambiarEstado(socio.getId(), "activo");
            }
        } else {
            acceso.setEsExitoso(false);
            respuesta.setEsExitoso(false);
            respuesta.setMensajeSemaforo("luz roja: membresia vencida o no encontrada");

            if (!"inactivo".equals(socio.getEstado())) {
                socioService.cambiarEstado(socio.getId(), "inactivo");
            }
        }

        Acceso accesoGuardado = accesoRepository.save(acceso);
        respuesta.setId(accesoGuardado.getId());

        return respuesta;
    }

    // Nuevo: Método para obtener los últimos accesos y convertirlos a DTO
    public List<AccesoDTO> obtenerUltimosAccesos() {
        List<Acceso> ultimosAccesos = accesoRepository.findTop10ByOrderByFechaHoraDesc();
        
        return ultimosAccesos.stream().map(acceso -> {
            AccesoDTO dto = new AccesoDTO();
            dto.setId(acceso.getId());
            dto.setFechaHora(acceso.getFechaHora());
            dto.setEsExitoso(acceso.getEsExitoso());
            dto.setMensajeSemaforo(acceso.getEsExitoso() ? "Acceso Exitoso" : "Acceso Denegado"); // Mensaje genérico para la lista
            dto.setNombreSocio(acceso.getSocio().getNombre());
            dto.setSocioId(acceso.getSocio().getId());
            return dto;
        }).collect(Collectors.toList());
    }
}
