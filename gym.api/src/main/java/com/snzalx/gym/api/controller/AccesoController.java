package com.snzalx.gym.api.controller;

import com.snzalx.gym.api.dto.AccesoDTO;
import com.snzalx.gym.api.service.AccesoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accesos")
@Slf4j
@CrossOrigin(origins = "*")
public class AccesoController {

    private final AccesoService accesoService;

    public AccesoController(AccesoService accesoService) {
        this.accesoService = accesoService;
    }

    @PostMapping("/registrar/{qrToken}")
    public ResponseEntity<AccesoDTO> registrarAcceso(@PathVariable UUID qrToken) {
        log.info(">>>> ¡PETICIÓN RECIBIDA! Intentando registrar acceso para token: {}", qrToken);
        try {
            AccesoDTO acceso = accesoService.registrarAccesoEscaner(qrToken);
            return ResponseEntity.ok(acceso);
        } catch (Exception e) {
            log.error(">>>> ERROR EN REGISTRO: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint solicitado por DetalleMetricaScreen para el recuadro "Accesos Hoy"
    @GetMapping("/hoy")
    public ResponseEntity<List<AccesoDTO>> getAccesosHoy() {
        log.info("Dashboard: Solicitando lista detallada de accesos de hoy");
        return ResponseEntity.ok(accesoService.obtenerUltimosAccesos());
    }

    @GetMapping("/recientes")
    public ResponseEntity<List<AccesoDTO>> obtenerUltimosAccesos() {
        return ResponseEntity.ok(accesoService.obtenerUltimosAccesos());
    }

    @GetMapping("/count-hoy")
    public ResponseEntity<Long> getCountHoy() {
        return ResponseEntity.ok(accesoService.countAccessesToday());
    }
}