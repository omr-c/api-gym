package com.snzalx.gym.api.controller;

import com.snzalx.gym.api.dto.AccesoDTO;
import com.snzalx.gym.api.service.AccesoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/accesos")
public class AccesoController {

    private final AccesoService accesoService;

    public AccesoController(AccesoService accesoService) {
        this.accesoService = accesoService;
    }

    // endpoint que consume el escaner al leer el codigo qr
    @PostMapping("/escanear/{qrToken}")
    public ResponseEntity<AccesoDTO> escanearQr(@PathVariable UUID qrToken) {
        AccesoDTO resultado = accesoService.registrarAccesoEscaner(qrToken);
        return ResponseEntity.ok(resultado);
    }
}