package com.snzalx.gym.api.controller;

import com.snzalx.gym.api.dto.AccesoDiarioDTO;
import com.snzalx.gym.api.dto.DashboardResumenDTO;
import com.snzalx.gym.api.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*") // INDISPENSABLE para que tu Motorola conecte
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // CORRECCIÓN: El frontend busca "/accesos", no "/accesos-semanales"
    @GetMapping("/accesos")
    public ResponseEntity<List<AccesoDiarioDTO>> getAccesos(@RequestParam(defaultValue = "semana") String rango) {
        // Aquí puedes usar el rango para filtrar en tu servicio si lo deseas
        return ResponseEntity.ok(dashboardService.getAccesosSemanales());
    }

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenDTO> obtenerResumen() {
        return ResponseEntity.ok(dashboardService.getResumenDashboard());
    }
}