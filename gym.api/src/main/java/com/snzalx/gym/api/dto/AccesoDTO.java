package com.snzalx.gym.api.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AccesoDTO {
    private UUID id;
    private String nombreSocio;
    private LocalDateTime fechaAcceso;

    // Estos son los dos campos que te estaban dando el error:
    private String mensajeSemaforo;
    private boolean esExitoso;

    // Si no usas @Data de Lombok, asegúrate de tener los Getter y Setter manuales:
    public String getMensajeSemaforo() { return mensajeSemaforo; }
    public void setMensajeSemaforo(String mensajeSemaforo) { this.mensajeSemaforo = mensajeSemaforo; }

    public boolean isEsExitoso() { return esExitoso; }
    public void setEsExitoso(boolean esExitoso) { this.esExitoso = esExitoso; }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombreSocio() { return nombreSocio; }
    public void setNombreSocio(String nombreSocio) { this.nombreSocio = nombreSocio; }

    public LocalDateTime getFechaAcceso() { return fechaAcceso; }
    public void setFechaAcceso(LocalDateTime fechaAcceso) { this.fechaAcceso = fechaAcceso; }
}