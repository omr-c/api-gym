package com.snzalx.gym.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "socios") // Esta sera la tabla en Supabase
@Data
public class Socio {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nombre;
    private String telefono;
    private String estado;
}