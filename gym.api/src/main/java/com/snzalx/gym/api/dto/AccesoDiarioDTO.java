package com.snzalx.gym.api.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AccesoDiarioDTO {
    private LocalDate fecha;
    private Long conteoAccesos;

    public AccesoDiarioDTO(LocalDate fecha, Long conteoAccesos) {
        this.fecha = fecha;
        this.conteoAccesos = conteoAccesos;
    }
}
