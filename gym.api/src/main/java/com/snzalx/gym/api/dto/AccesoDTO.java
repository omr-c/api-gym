package com.snzalx.gym.api.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

// dto para responder al escaner con la semaforizacion
// se agrego la propiedad socioid para el proceso de pago
@Data
public class AccesoDTO {
    private UUID id;
    private LocalDateTime fechaHora;
    private Boolean esExitoso;
    private String mensajeSemaforo;
    private String nombreSocio;
    private UUID socioId; // <--- AGREGADO PARA EL FRONTEND
}
 
