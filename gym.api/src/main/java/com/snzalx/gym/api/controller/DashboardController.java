package com.snzalx.gym.api.controller;

import com.snzalx.gym.api.dto.AccesoDiarioDTO;
import com.snzalx.gym.api.dto.DashboardResumenDTO;
import com.snzalx.gym.api.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
