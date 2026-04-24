package com.snzalx.gym.api.controller;

import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.service.SocioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/socios")
public class SocioController {

    private final SocioService socioService;

    public SocioController(SocioService socioService) {
        this.socioService = socioService;
    }

    // endpoint para que el usuario se autoregistre desde la app
    @PostMapping("/registro")
    public ResponseEntity<Socio> crearSocio(@RequestBody Socio socio) {
        Socio nuevoSocio = socioService.registrarSocio(socio);
        return ResponseEntity.ok(nuevoSocio);
    }

    // endpoint administrativo para dar de baja o reactivar
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Socio> actualizarEstado(
            @PathVariable UUID id,
            @RequestParam String estado) {
        return ResponseEntity.ok(socioService.cambiarEstado(id, estado));
    }
}