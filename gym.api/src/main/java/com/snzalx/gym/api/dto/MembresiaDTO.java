package com.snzalx.gym.api.dto;

import lombok.Data;
import java.math.BigDecimal; // Importar BigDecimal
import java.time.LocalDate;
import java.util.UUID;

@Data
public class MembresiaDTO {
    private UUID socioId;
    private BigDecimal monto; // Cambiado de Double a BigDecimal
    private String metodoPago;
    private Integer duracionDias; // Ejemplo: 30 para un mes
    
    // Campos que el servidor devolverá
    private UUID id;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
}
