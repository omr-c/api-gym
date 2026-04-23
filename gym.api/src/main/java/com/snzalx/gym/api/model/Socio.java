package com.snzalx.gym.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "socios")
@Data
public class Socio {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nombre;
    private String telefono;
    private String email;
    private String fotoUrl;
    
    @Column(unique = true)
    private UUID qrToken;
    
    private String bio;
    private String instagramUrl;
    private String estado;
}
