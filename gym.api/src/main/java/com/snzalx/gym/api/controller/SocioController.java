package com.snzalx.gym.api.controller;

import com.snzalx.gym.api.dto.SocioDTO;
import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.service.SocioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/socios")
@CrossOrigin(origins = "*") // Habilitar CORS para este controlador
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
    public ResponseEntity<List<SocioDTO>> listarActivos() {
        return ResponseEntity.ok(socioService.listarActivos());
    }

    // Nuevo: Endpoint para obtener el perfil completo del socio con diasRestantes
    @GetMapping("/perfil/{qrToken}")
    public ResponseEntity<SocioDTO> obtenerPerfilSocio(@PathVariable UUID qrToken) {
        try {
            SocioDTO socioDTO = socioService.obtenerSocioDtoPorQr(qrToken);
            return ResponseEntity.ok(socioDTO);
        } catch (RuntimeException e) {
            // Si el socio no es encontrado, devolver 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
