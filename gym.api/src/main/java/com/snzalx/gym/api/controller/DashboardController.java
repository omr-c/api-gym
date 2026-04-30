package com.snzalx.gym.api.controller;

import com.snzalx.gym.api.dto.AccesoDiarioDTO;
import com.snzalx.gym.api.dto.DashboardResumenDTO;
import com.snzalx.gym.api.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // Endpoint para la gráfica de accesos semanales
    @GetMapping("/accesos-semanales")
    public ResponseEntity<List<AccesoDiarioDTO>> getAccesosSemanales() {
        return ResponseEntity.ok(dashboardService.getAccesosSemanales());
    }

    // Endpoint para el resumen de métricas (socios e ingresos)
    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenDTO> getResumen() {
        return ResponseEntity.ok(dashboardService.getResumenDashboard());
    }



    // endpoint de resumen general (se mantiene intacto)
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> obtenerResumen() {
        Map<String, Object> resumen = new HashMap<>();

        resumen.put("totalSociosActivos", 24);
        resumen.put("totalSociosPendientes", 3);
        resumen.put("ingresosHoy", 1250.0);
        resumen.put("ingresosSemana", 8500.0);

        return ResponseEntity.ok(resumen);
    }

    // endpoint unificado y dinamico que reacciona a los filtros
    @GetMapping("/accesos")
    public ResponseEntity<List<Map<String, Object>>> obtenerAccesosDinamicos(
            @RequestParam(defaultValue = "semana") String rango) {

        List<Map<String, Object>> accesos = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        if ("mes".equalsIgnoreCase(rango)) {
            // si piden mes, generamos los ultimos 30 dias
            for (int i = 29; i >= 0; i--) {
                Map<String, Object> punto = new HashMap<>();
                punto.put("fecha", hoy.minusDays(i).toString());
                punto.put("conteoAccesos", 15 + (int)(Math.random() * 50));
                accesos.add(punto);
            }
        } else if ("ano".equalsIgnoreCase(rango)) {
            // si piden año, generamos las 12 barras correspondientes a los meses
            String[] nombresMeses = {"ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"};
            for (int i = 0; i < 12; i++) {
                Map<String, Object> punto = new HashMap<>();
                punto.put("fecha", nombresMeses[i]);
                punto.put("conteoAccesos", 300 + (int)(Math.random() * 400));
                accesos.add(punto);
            }
        } else {
            // por defecto siempre devuelve los 7 dias de la semana
            for (int i = 6; i >= 0; i--) {
                Map<String, Object> punto = new HashMap<>();
                punto.put("fecha", hoy.minusDays(i).toString());
                punto.put("conteoAccesos", 10 + (int)(Math.random() * 30));
                accesos.add(punto);
            }
        }

        return ResponseEntity.ok(accesos);
    }
}
