package com.snzalx.gym.api.controller;

import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private SocioRepository socioRepository;

    @GetMapping("/verificar/{email}")
    public ResponseEntity<?> verificarUsuario(@PathVariable String email) {
        Optional<Socio> socioOpt = socioRepository.findByEmail(email);
        Map<String, Object> respuesta = new HashMap<>();

        if (socioOpt.isPresent()) {
            Socio socio = socioOpt.get();
            respuesta.put("existe", true);
            respuesta.put("nombre", socio.getNombre());
            respuesta.put("rol", socio.getRol());
            respuesta.put("qrToken", socio.getQrToken());

            // CORRECCIÓN CRÍTICA: Enviar el estado real al Socio
            respuesta.put("estado", socio.getEstado());

            return ResponseEntity.ok(respuesta);
        } else {
            respuesta.put("existe", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }
}