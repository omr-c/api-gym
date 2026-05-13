package com.snzalx.gym.api.controller;

import com.snzalx.gym.api.dto.PagoRecienteDTO;
import com.snzalx.gym.api.dto.ResumenPagosDTO;
import com.snzalx.gym.api.service.PagoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@Slf4j
@CrossOrigin(origins = "*")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenPagosDTO> getResumenPagos(@RequestParam(defaultValue = "mes") String rango) {
        log.info("Dashboard: Solicitando resumen de pagos para: {}", rango);
        return ResponseEntity.ok(pagoService.getResumenPagos(rango));
    }

    @GetMapping("/recientes")
    public ResponseEntity<List<PagoRecienteDTO>> getPagosRecientes(@RequestParam(defaultValue = "mes") String rango) {
        log.info("Dashboard: Solicitando lista de cobros para: {}", rango);
        return ResponseEntity.ok(pagoService.getPagosRecientes(rango));
    }

    @GetMapping("/historial-mensual")
    public ResponseEntity<List<Map<String, Object>>> getHistorialMensual() {
        log.info("Dashboard: Solicitando historial de meses anteriores");
        return ResponseEntity.ok(pagoService.getHistorialMensual());
    }
}