package com.snzalx.gym.api.controller;

import com.snzalx.gym.api.model.Membresia;
import com.snzalx.gym.api.repository.MembresiaRepository;
import com.snzalx.gym.api.service.AccesoService;
import com.snzalx.gym.api.service.SocioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Slf4j
@CrossOrigin(origins = "*")
public class AdminController {

    private final SocioService socioService;
    private final AccesoService accesoService;
    private final MembresiaRepository membresiaRepository;

    public AdminController(SocioService socioService, AccesoService accesoService, MembresiaRepository membresiaRepository) {
        this.socioService = socioService;
        this.accesoService = accesoService;
        this.membresiaRepository = membresiaRepository;
    }

    @GetMapping("/resumen-dashboard")
    public ResponseEntity<Map<String, Object>> getResumenDashboard() {
        log.info("Dashboard: Solicitando resumen general.");
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("totalSocios", socioService.findAllSocios().size());
        resumen.put("sociosActivos", socioService.listarActivos());
        resumen.put("sociosVencidos", socioService.countSociosWithExpiredMembership());
        resumen.put("accesosHoy", accesoService.countAccessesToday());
        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/estadisticas-accesos")
    public ResponseEntity<List<Map<String, Object>>> getEstadisticasAccesos(@RequestParam(defaultValue = "semana") String rango) {
        log.info("Dashboard: Solicitando estadísticas de accesos para rango: {}", rango);
        try {
            List<Map<String, Object>> stats = accesoService.getAccessStatisticsByRange(rango);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error en estadísticas: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/resumen-pagos")
    public ResponseEntity<Map<String, Object>> getResumenPagos() {
        log.info("Dashboard: Solicitando resumen de pagos.");
        try {
            Map<String, Object> resumen = new HashMap<>();
            List<Membresia> todas = membresiaRepository.findAll();
            BigDecimal ingresosTotales = todas.stream()
                    .map(Membresia::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            resumen.put("ingresosTotales", ingresosTotales);
            resumen.put("totalTransacciones", todas.size());
            resumen.put("pagosRecientes", todas.size() > 5 ? todas.subList(Math.max(0, todas.size() - 5), todas.size()) : todas);
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            log.error("Error en resumen de pagos: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}