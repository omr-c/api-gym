package com.snzalx.gym.api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PagoRecienteDTO {
    private UUID id;
    private String nombreSocio;
    private BigDecimal monto;
    private String fechaFormateada; // Este es el campo que faltaba
}