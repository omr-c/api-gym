package com.snzalx.gym.api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RegistroPagoDTO {
    private UUID socioId;
    private BigDecimal monto;
    // Puedes añadir más campos si son necesarios, como tipo de membresía, etc.
}
