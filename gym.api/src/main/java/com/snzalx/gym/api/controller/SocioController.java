package com.snzalx.gym.api.controller;

import com.snzalx.gym.api.dto.MembresiaDTO;
import com.snzalx.gym.api.dto.SocioDTO;
import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.service.MembresiaService;
import com.snzalx.gym.api.service.SocioService;
import com.snzalx.gym.api.repository.SocioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/socios")
@CrossOrigin(origins = "*")
@Slf4j
public class SocioController {

    private final SocioService socioService;
    private final SocioRepository socioRepository;
    private final MembresiaService membresiaService;

    public SocioController(SocioService socioService, SocioRepository socioRepository, MembresiaService membresiaService) {
        this.socioService = socioService;
        this.socioRepository = socioRepository;
        this.membresiaService = membresiaService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<SocioDTO> registrarSocio(@RequestBody Socio socio) {
        log.info("Registrando nuevo socio: {}", socio.getNombre());
        return ResponseEntity.ok(socioService.registrarSocio(socio));
    }

    // ENDPOINT PARA EL RECUADRO "SOCIOS" (TODOS)
    @GetMapping("/all")
    public ResponseEntity<List<SocioDTO>> getAllSocios() {
        log.info("Dashboard: Solicitando lista completa de socios");
        return ResponseEntity.ok(socioService.findAllSocios());
    }

    // ENDPOINT PARA EL RECUADRO "ACTIVOS"
    @GetMapping("/activos")
    public ResponseEntity<List<SocioDTO>> getSociosActivos() {
        log.info("Dashboard: Filtrando socios activos");
        // Reutilizamos el servicio para convertir a DTO automáticamente
        List<SocioDTO> activos = socioRepository.findByEstadoIgnoreCase("activo")
                .stream()
                .map(socioService::convertToDTO)
                .toList();
        return ResponseEntity.ok(activos);
    }

    // ENDPOINT PARA EL RECUADRO "VENCIDOS"
    @GetMapping("/vencidos")
    public ResponseEntity<List<SocioDTO>> getSociosVencidos() {
        log.info("Dashboard: Filtrando socios vencidos");
        List<SocioDTO> vencidos = socioRepository.findByEstadoIgnoreCase("vencido")
                .stream()
                .map(socioService::convertToDTO)
                .toList();
        return ResponseEntity.ok(vencidos);
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<MembresiaDTO> procesarPago(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> payload) {
        try {
            log.info("Petición de pago recibida para socio ID: {}", id);
            Object montoObj = payload.get("monto");
            BigDecimal monto = new BigDecimal(montoObj.toString());

            MembresiaDTO nuevaMembresia = membresiaService.activarMembresiaPorPago(id, monto);
            return ResponseEntity.ok(nuevaMembresia);
        } catch (Exception e) {
            log.error("Error al procesar pago: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/perfil/{qrToken}")
    public ResponseEntity<SocioDTO> obtenerPerfilSocio(@PathVariable UUID qrToken) {
        try {
            return ResponseEntity.ok(socioService.obtenerSocioDtoPorQr(qrToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/perfil-por-email")
    public ResponseEntity<SocioDTO> obtenerPerfilPorEmail(@RequestParam("email") String email) {
        return socioRepository.findByEmail(email)
                .map(socio -> ResponseEntity.ok(socioService.convertToDTO(socio)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}