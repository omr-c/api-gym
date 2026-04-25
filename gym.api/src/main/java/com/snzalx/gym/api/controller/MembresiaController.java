package com.snzalx.gym.api.controller;

import com.snzalx.gym.api.dto.MembresiaDTO;
import com.snzalx.gym.api.service.MembresiaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/membresias")
public class MembresiaController {

    private final MembresiaService membresiaService;

    public MembresiaController(MembresiaService membresiaService) {
        this.membresiaService = membresiaService;
    }

    // Endpoint para que el Admin registre un pago desde la App Flutter (Recepción)
    @PostMapping("/pagar")
    public ResponseEntity<MembresiaDTO> registrarPago(@RequestBody MembresiaDTO dto) {
        MembresiaDTO respuesta = membresiaService.registrarPago(dto);
        return ResponseEntity.ok(respuesta);
    }
}
