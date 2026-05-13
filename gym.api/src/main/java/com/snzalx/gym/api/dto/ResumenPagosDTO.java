package com.snzalx.gym.api.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ResumenPagosDTO {
    private BigDecimal totalRecaudado;
    private BigDecimal metaMensual; // Placeholder, se puede configurar o calcular
}
