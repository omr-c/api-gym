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

    @PostMapping("/solicitar-codigo")
    public ResponseEntity<String> solicitarCodigo(@RequestParam String email, @RequestParam String telefono) {
        try {
            socioService.solicitarCodigo(email, telefono);
            return ResponseEntity.ok("Código enviado exitosamente");
        } catch (IllegalArgumentException e) {
            // Devuelve 409 Conflict si los datos ya existen
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            // Devuelve 500 si falla el envío de correo
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    @PostMapping("/validar-codigo")
    public ResponseEntity<Boolean> validarCodigo(@RequestParam String email, @RequestParam String codigo) {
        boolean esValido = socioService.validarCodigo(email, codigo);
        return ResponseEntity.ok(esValido);
    }

    @PostMapping("/registrar")
    public ResponseEntity<Socio> registrar(@RequestBody Socio socio) {
        try {
            Socio registrado = socioService.registrarSocio(socio);
            return ResponseEntity.status(HttpStatus.CREATED).body(registrado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // --- NUEVO ENDPOINT PARA EDITAR PERFIL ---
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Socio> actualizarPerfil(@PathVariable UUID id, @RequestBody Socio socioData) {
        try {
            Socio socioActualizado = socioService.actualizarPerfil(id, socioData.getNombre(), socioData.getFotoUrl());
            return ResponseEntity.ok(socioActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    // -----------------------------------------

    // Endpoint para el panel administrativo
    @GetMapping("/activos")
    public ResponseEntity<List<SocioDTO>> listarActivos() {
        return ResponseEntity.ok(socioService.listarActivos());
    }

    // Endpoint para obtener el perfil completo del socio con diasRestantes
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