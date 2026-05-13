package com.snzalx.gym.api.service;

import com.snzalx.gym.api.dto.PagoRecienteDTO;
import com.snzalx.gym.api.dto.ResumenPagosDTO;
import com.snzalx.gym.api.model.Membresia;
import com.snzalx.gym.api.repository.MembresiaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PagoService {

    private final MembresiaRepository membresiaRepository;

    public PagoService(MembresiaRepository membresiaRepository) {
        this.membresiaRepository = membresiaRepository;
    }

    public ResumenPagosDTO getResumenPagos(String rango) {
        LocalDate hoy = LocalDate.now();
        LocalDate startDate = calcularFechaInicio(rango, hoy);
        BigDecimal metaMensual = new BigDecimal("20000.00");

        List<Membresia> membresiasEnRango = membresiaRepository.findByFechaInicioBetween(startDate, hoy);

        BigDecimal totalRecaudado = membresiasEnRango.stream()
                .map(Membresia::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ResumenPagosDTO resumen = new ResumenPagosDTO();
        resumen.setTotalRecaudado(totalRecaudado);
        resumen.setMetaMensual(metaMensual);
        return resumen;
    }

    public List<PagoRecienteDTO> getPagosRecientes(String rango) {
        LocalDate hoy = LocalDate.now();
        LocalDate startDate = calcularFechaInicio(rango, hoy);

        // Filtramos las membresías según el rango seleccionado en la App
        return membresiaRepository.findByFechaInicioBetween(startDate, hoy).stream()
                .sorted(Comparator.comparing(Membresia::getFechaInicio).reversed())
                .limit(15) // Un poco más de 10 para llenar la lista
                .map(this::convertToPagoRecienteDTO)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getHistorialMensual() {
        List<Object[]> resultados = membresiaRepository.getHistorialMensualCifras();
        List<Map<String, Object>> historial = new ArrayList<>();

        for (Object[] fila : resultados) {
            Map<String, Object> mesData = new HashMap<>();
            mesData.put("mes", obtenerNombreMes((String) fila[0]));
            mesData.put("anio", fila[1]);
            mesData.put("total", fila[2]);
            historial.add(mesData);
        }
        return historial;
    }

    private LocalDate calcularFechaInicio(String rango, LocalDate hoy) {
        switch (rango.toLowerCase()) {
            case "hoy": return hoy;
            case "semana": return hoy.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            case "mes": return hoy.with(TemporalAdjusters.firstDayOfMonth());
            default: return hoy.with(TemporalAdjusters.firstDayOfMonth());
        }
    }

    private String obtenerNombreMes(String numMes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[Integer.parseInt(numMes) - 1];
    }

    private PagoRecienteDTO convertToPagoRecienteDTO(Membresia membresia) {
        PagoRecienteDTO dto = new PagoRecienteDTO();
        dto.setId(membresia.getId());
        dto.setNombreSocio(membresia.getSocio().getNombre());
        dto.setMonto(membresia.getMonto());
        dto.setFechaFormateada(membresia.getFechaInicio().toString());
        return dto;
    }
}