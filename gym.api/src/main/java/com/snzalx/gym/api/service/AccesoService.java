package com.snzalx.gym.api.service;

import com.snzalx.gym.api.dto.AccesoDTO;
import com.snzalx.gym.api.model.Acceso;
import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.repository.AccesoRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AccesoService {

    // inyeccion de repositorios y servicios
    private final AccesoRepository accesoRepository;
    private final SocioService socioService;

    public AccesoService(AccesoRepository accesoRepository, SocioService socioService) {
        this.accesoRepository = accesoRepository;
        this.socioService = socioService;
    }

    // logica principal del escaner de recepcion
    public AccesoDTO registrarAccesoEscaner(UUID qrToken) {
        Socio socio = socioService.obtenerPorQr(qrToken);

        Acceso acceso = new Acceso();
        acceso.setSocio(socio);
        acceso.setFechaHora(LocalDateTime.now());

        AccesoDTO respuesta = new AccesoDTO();
        respuesta.setNombreSocio(socio.getNombre());
        respuesta.setFechaHora(acceso.getFechaHora());

        // semaforizacion basica basada en el estado del socio
        // alx complementara esto despues con la fecha de la membresia
        if ("activo".equalsIgnoreCase(socio.getEstado())) {
            acceso.setEsExitoso(true);
            respuesta.setEsExitoso(true);
            respuesta.setMensajeSemaforo("luz verde: acceso permitido");
        } else {
            acceso.setEsExitoso(false);
            respuesta.setEsExitoso(false);
            respuesta.setMensajeSemaforo("luz roja: membresia inactiva o vencida");
        }

        // guardamos el historial en base de datos
        Acceso accesoGuardado = accesoRepository.save(acceso);
        respuesta.setId(accesoGuardado.getId());

        return respuesta;
    }
}