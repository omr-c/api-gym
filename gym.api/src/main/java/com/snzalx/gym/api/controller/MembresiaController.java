package com.snzalx.gym.api.controller;

import com.snzalx.gym.api.dto.MembresiaDTO;
import com.snzalx.gym.api.model.Membresia;
import com.snzalx.gym.api.repository.MembresiaRepository;
import com.snzalx.gym.api.service.MembresiaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/membresias")
public class MembresiaController {

    private final MembresiaService membresiaService;
    private final MembresiaRepository membresiaRepository;

    public MembresiaController(MembresiaService membresiaService, MembresiaRepository membresiaRepository) {
        this.membresiaService = membresiaService;
        this.membresiaRepository = membresiaRepository;
    }

    @PostMapping("/pagar")
    public ResponseEntity<MembresiaDTO> registrarPago(@RequestBody MembresiaDTO dto) {
        MembresiaDTO respuesta = membresiaService.registrarPago(dto);
        return ResponseEntity.ok(respuesta);
    }

    // Nuevo: Historial de pagos de un socio
    @GetMapping("/historial/{socioId}")
    public ResponseEntity<List<Membresia>> obtenerHistorial(@PathVariable UUID socioId) {
        return ResponseEntity.ok(membresiaRepository.findBySocioId(socioId));
    }

    // Nuevo: Próximos vencimientos (ej. próximos 7 días)
    @GetMapping("/vencimientos-proximos")
    public ResponseEntity<List<Membresia>> obtenerProximosVencimientos() {
        LocalDate hoy = LocalDate.now();
        LocalDate proximaSemana = hoy.plusDays(7);
        return ResponseEntity.ok(membresiaRepository.findByFechaVencimientoBetween(hoy, proximaSemana));
    }
}
