package com.snzalx.gym.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accesos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Acceso {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "socio_id", nullable = false)
    private Socio socio;

    private LocalDateTime fechaAcceso;
    private Boolean esExitoso; // Nuevo campo
    private String mensajeSemaforo; // Nuevo campo

    @PrePersist
    protected void onCreate() {
        if (fechaAcceso == null) {
            fechaAcceso = LocalDateTime.now();
        }
    }
}
