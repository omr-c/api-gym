package com.snzalx.gym.api.dto;

import lombok.Data;

@Data
public class DashboardResumenDTO {
    private Long totalSociosActivos;
    private Long totalSociosPendientes;
    private Long totalSociosInactivos;
    private Double ingresosHoy;
    private Double ingresosSemana;
}
