package com.snzalx.gym.api.service;

import com.snzalx.gym.api.dto.AccesoDiarioDTO;
import com.snzalx.gym.api.dto.DashboardResumenDTO;
import com.snzalx.gym.api.model.Acceso;
import com.snzalx.gym.api.model.Membresia;
import com.snzalx.gym.api.repository.AccesoRepository;
import com.snzalx.gym.api.repository.MembresiaRepository;
import com.snzalx.gym.api.repository.SocioRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DashboardService {

    private final AccesoRepository accesoRepository;
    private final MembresiaRepository membresiaRepository;
    private final SocioRepository socioRepository;

    public DashboardService(AccesoRepository accesoRepository, MembresiaRepository membresiaRepository, SocioRepository socioRepository) {
        this.accesoRepository = accesoRepository;
        this.membresiaRepository = membresiaRepository;
        this.socioRepository = socioRepository;
    }

    public List<AccesoDiarioDTO> getAccesosSemanales() {
        LocalDate hoy = LocalDate.now();
        LocalDate haceUnaSemana = hoy.minusDays(6); // Últimos 7 días incluyendo hoy

        List<Acceso> accesosUltimaSemana = accesoRepository.findByFechaHoraBetween(
                haceUnaSemana.atStartOfDay(),
                hoy.atTime(LocalTime.MAX)
        );

        // Agrupar accesos por fecha y contar
        Map<LocalDate, Long> conteoPorDia = accesosUltimaSemana.stream()
                .collect(Collectors.groupingBy(
                        acceso -> acceso.getFechaHora().toLocalDate(),
                        Collectors.counting()
                ));

        // Llenar los días sin accesos con conteo 0
        List<AccesoDiarioDTO> accesosDiarios = new ArrayList<>();
        IntStream.rangeClosed(0, 6).forEach(i -> {
            LocalDate fecha = haceUnaSemana.plusDays(i);
            accesosDiarios.add(new AccesoDiarioDTO(fecha, conteoPorDia.getOrDefault(fecha, 0L)));
        });

        return accesosDiarios;
    }

    public DashboardResumenDTO getResumenDashboard() {
        DashboardResumenDTO resumen = new DashboardResumenDTO();

        // Conteo de socios por estado
        resumen.setTotalSociosActivos(socioRepository.countByEstadoIgnoreCase("activo"));
        resumen.setTotalSociosPendientes(socioRepository.countByEstadoIgnoreCase("pendiente"));
        resumen.setTotalSociosInactivos(socioRepository.countByEstadoIgnoreCase("inactivo"));

        // Ingresos del día
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioHoy = hoy.atStartOfDay();
        LocalDateTime finHoy = hoy.atTime(LocalTime.MAX);
        Double ingresosHoy = membresiaRepository.findByFechaInicioBetween(inicioHoy.toLocalDate(), finHoy.toLocalDate())
                .stream()
                .mapToDouble(Membresia::getMonto)
                .sum();
        resumen.setIngresosHoy(ingresosHoy);

        // Ingresos de la semana (Lunes a Domingo)
        LocalDate inicioSemana = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate finSemana = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        Double ingresosSemana = membresiaRepository.findByFechaInicioBetween(inicioSemana, finSemana)
                .stream()
                .mapToDouble(Membresia::getMonto)
                .sum();
        resumen.setIngresosSemana(ingresosSemana);

        return resumen;
    }
}
