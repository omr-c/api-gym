package com.snzalx.gym.api.dto;

import lombok.Data;

@Data
public class AccesoDiarioDTO {
    private String fecha; // Cambiado de LocalDate a String para Flutter
    private Long conteoAccesos;

    public AccesoDiarioDTO(String fecha, Long conteoAccesos) {
        this.fecha = fecha;
        this.conteoAccesos = conteoAccesos;
    }
}
