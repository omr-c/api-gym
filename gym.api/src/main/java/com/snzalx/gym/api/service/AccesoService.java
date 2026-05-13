package com.snzalx.gym.api.service;

import com.snzalx.gym.api.dto.AccesoDTO;
import com.snzalx.gym.api.model.Acceso;
import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.repository.AccesoRepository;
import com.snzalx.gym.api.repository.SocioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccesoService {

    private final AccesoRepository accesoRepository;
    private final SocioRepository socioRepository;

    public AccesoService(AccesoRepository accesoRepository, SocioRepository socioRepository) {
        this.accesoRepository = accesoRepository;
        this.socioRepository = socioRepository;
    }

    public long countAccessesToday() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return accesoRepository.countByFechaAccesoBetween(startOfDay, endOfDay);
    }

    public List<Map<String, Object>> getAccessStatisticsByRange(String rango) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;

        switch (rango.toLowerCase()) {
            case "semana":
                startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
                break;
            case "mes":
                startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
                break;
            default:
                throw new IllegalArgumentException("Rango no válido: " + rango);
        }

        List<Object[]> results = accesoRepository.countAccessesByDay(startDate, now);
        return results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("etiqueta", (String) result[0]);
                    map.put("conteoAccesos", ((Number) result[1]).intValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public AccesoDTO registrarAccesoEscaner(UUID qrToken) {
        log.info("Iniciando registro de acceso para QR: {}", qrToken);

        Socio socio = socioRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado con QR: " + qrToken));

        Acceso nuevoAcceso = new Acceso();
        nuevoAcceso.setSocio(socio);
        
        // CORRECCIÓN RADICAL:
        // Limpiamos el texto de cualquier espacio invisible y lo pasamos a minúsculas
        String estadoLimpio = socio.getEstado() != null ? socio.getEstado().trim().toLowerCase() : "vencido";
        log.info("Socio encontrado: {}. Estado en Base de Datos (limpio): [{}]", socio.getNombre(), estadoLimpio);
        
        // Validamos si el estado CONTIENE la palabra 'act' (para atrapar 'activo', 'Activo ', etc.)
        boolean esActivo = estadoLimpio.contains("act"); 

        nuevoAcceso.setEsExitoso(esActivo);
        nuevoAcceso.setMensajeSemaforo(esActivo ? "ACCESO AUTORIZADO" : "DENEGADO - ESTADO: " + estadoLimpio.toUpperCase());

        Acceso accesoGuardado = accesoRepository.save(nuevoAcceso);
        log.info("Acceso guardado con éxito. ID: {} - Resultado: {}", accesoGuardado.getId(), esActivo);

        return convertToAccesoDTO(accesoGuardado);
    }

    public List<AccesoDTO> obtenerUltimosAccesos() {
        return accesoRepository.findTop10ByOrderByFechaAccesoDesc().stream()
                .map(this::convertToAccesoDTO)
                .collect(Collectors.toList());
    }

    private AccesoDTO convertToAccesoDTO(Acceso acceso) {
        AccesoDTO dto = new AccesoDTO();
        dto.setId(acceso.getId());
        dto.setNombreSocio(acceso.getSocio().getNombre());
        dto.setFechaAcceso(acceso.getFechaAcceso());
        
        // Evitamos nulos para que no explote el front
        dto.setEsExitoso(acceso.getEsExitoso() != null && acceso.getEsExitoso());
        dto.setMensajeSemaforo(acceso.getMensajeSemaforo() != null ? acceso.getMensajeSemaforo() : "SIN MENSAJE");
        
        return dto;
    }
}
