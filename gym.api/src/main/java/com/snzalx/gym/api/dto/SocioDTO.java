package com.snzalx.gym.api.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class SocioDTO {
    private UUID id;
    private String nombre;
    private String telefono;
    private String email;
    private String fotoUrl;
    private UUID qrToken;
    private String bio;
    private String instagramUrl;
    private String estado;
    private String rol;
    private Long diasRestantes; // Campo calculado para las alertas de color en el Front
}