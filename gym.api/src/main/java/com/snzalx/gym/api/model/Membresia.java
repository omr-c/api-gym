package com.snzalx.gym.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "membresias")
@Data
public class Membresia {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private Double monto;
    private String metodoPago;

    @ManyToOne
    @JoinColumn(name = "socio_id")
    private Socio socio;
}
