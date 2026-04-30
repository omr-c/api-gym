package com.snzalx.gym.api.service;

import com.snzalx.gym.api.dto.AccesoDiarioDTO;
import com.snzalx.gym.api.dto.DashboardResumenDTO;
import com.snzalx.gym.api.model.Acceso;
import com.snzalx.gym.api.model.Membresia;
import com.snzalx.gym.api.repository.AccesoRepository;
import com.snzalx.gym.api.repository.MembresiaRepository;
import com.snzalx.gym.api.repository.SocioRepository;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        LocalDate haceUnaSemana = hoy.minusDays(6);

        List<Acceso> accesosUltimaSemana = accesoRepository.findByFechaHoraBetween(
                haceUnaSemana.atStartOfDay(),
                hoy.atTime(LocalTime.MAX)
        );

        // Agrupar y asegurar que los días vacíos tengan 0
        Map<LocalDate, Long> conteoPorDia = accesosUltimaSemana.stream()
                .collect(Collectors.groupingBy(
                        acceso -> acceso.getFechaHora().toLocalDate(),
                        Collectors.counting()
                ));

        List<AccesoDiarioDTO> respuesta = new ArrayList<>();
        IntStream.rangeClosed(0, 6).forEach(i -> {
            LocalDate fecha = haceUnaSemana.plusDays(i);
            // Formateamos la fecha como String YYYY-MM-DD para Flutter
            respuesta.add(new AccesoDiarioDTO(fecha.toString(), conteoPorDia.getOrDefault(fecha, 0L)));
        });

        return respuesta;
    }

    public DashboardResumenDTO getResumenDashboard() {
        DashboardResumenDTO resumen = new DashboardResumenDTO();

        // OPTIMIZACIÓN: Una sola consulta para todos los estados
        List<Object[]> resultados = socioRepository.countAllSociosByEstado();
        long activos = 0, pendientes = 0, inactivos = 0;
        
        for (Object[] fila : resultados) {
            String estado = (String) fila[0];
            Long conteo = (Long) fila[1];
            if ("activo".equalsIgnoreCase(estado)) activos = conteo;
            else if ("pendiente".equalsIgnoreCase(estado)) pendientes = conteo;
            else if ("inactivo".equalsIgnoreCase(estado)) inactivos = conteo;
        }

        resumen.setTotalSociosActivos(activos);
        resumen.setTotalSociosPendientes(pendientes);
        resumen.setTotalSociosInactivos(inactivos);

        // Ingresos
        LocalDate hoy = LocalDate.now();
        resumen.setIngresosHoy(sumarIngresos(hoy, hoy));
        
        LocalDate inicioSemana = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        resumen.setIngresosSemana(sumarIngresos(inicioSemana, hoy));

        return resumen;
    }

    private Double sumarIngresos(LocalDate inicio, LocalDate fin) {
        return membresiaRepository.findByFechaInicioBetween(inicio, fin)
                .stream()
                .mapToDouble(Membresia::getMonto)
                .sum();
    }
}
