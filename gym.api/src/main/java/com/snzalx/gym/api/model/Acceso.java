package com.snzalx.gym.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accesos")
@Data
public class Acceso {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDateTime fechaHora;
    private Boolean esExitoso;

    @ManyToOne
    @JoinColumn(name = "socio_id")
    private Socio socio;
}
