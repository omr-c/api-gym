package com.snzalx.gym.api.dto;

import lombok.Data;
import java.util.UUID;

// dto para transferir la informacion del socio a la app movil
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
}