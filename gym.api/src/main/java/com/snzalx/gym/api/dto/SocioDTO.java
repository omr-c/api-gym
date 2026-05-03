package com.snzalx.gym.api.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class SocioDTO { // Corregido: Eliminado el 'class' duplicado
    private UUID id;
    private String nombre;
    private String telefono;
    private String email;
    private String fotoUrl;
    private UUID qrToken;
    private String bio;
    private String instagramUrl;
    private String estado;
    private Long diasRestantes; // Nuevo campo calculado para el frontend
}
