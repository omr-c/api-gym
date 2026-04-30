package com.snzalx.gym.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "socios")
@Data
public class Socio {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String nombre;
    private String telefono;
    private String email;
    private String fotoUrl;

    @Column(unique = true)
    private UUID qrToken; // token para la identidad digital[cite: 3]

    private String bio;
    private String instagramUrl;
    private String estado; // activo o inactivo[cite: 6]

    // este campo es el que permite al logincontroller decidir la navegacion
    private String rol; // puede ser admin, recepcion o socio
}
