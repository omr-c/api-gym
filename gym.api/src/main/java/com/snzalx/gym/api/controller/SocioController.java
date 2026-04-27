package com.snzalx.gym.api.controller;

import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.service.SocioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/socios")
public class SocioController {

    private final SocioService socioService;

    public SocioController(SocioService socioService) {
        this.socioService = socioService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<Socio> registrar(@RequestBody Socio socio) {
        return ResponseEntity.ok(socioService.registrarSocio(socio));
    }

    // Nuevo: Endpoint para el panel administrativo
    @GetMapping("/activos")
    public ResponseEntity<List<Socio>> listarActivos() {
        return ResponseEntity.ok(socioService.listarActivos());
    }
}
