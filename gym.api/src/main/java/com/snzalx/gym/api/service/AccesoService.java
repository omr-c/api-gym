package com.snzalx.gym.api.service;

import com.snzalx.gym.api.dto.AccesoDTO;
import com.snzalx.gym.api.model.Acceso;
import com.snzalx.gym.api.model.Membresia;
import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.repository.AccesoRepository;
import com.snzalx.gym.api.repository.MembresiaRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccesoService {

    // inyeccion de repositorios y servicios
    private final AccesoRepository accesoRepository;
    private final SocioService socioService;
    private final MembresiaRepository membresiaRepository;

    public AccesoService(AccesoRepository accesoRepository,
                         SocioService socioService,
                         MembresiaRepository membresiaRepository) {
        this.accesoRepository = accesoRepository;
        this.socioService = socioService;
        this.membresiaRepository = membresiaRepository;
    }

    // logica principal del escaner con validacion de fechas de pago
    public AccesoDTO registrarAccesoEscaner(UUID qrToken) {
        Socio socio = socioService.obtenerPorQr(qrToken);

        Acceso acceso = new Acceso();
        acceso.setSocio(socio);
        acceso.setFechaHora(LocalDateTime.now());

        AccesoDTO respuesta = new AccesoDTO();
        respuesta.setNombreSocio(socio.getNombre());
        respuesta.setFechaHora(acceso.getFechaHora());

        // buscamos el historial de membresias del socio
        List<Membresia> membresias = membresiaRepository.findBySocioId(socio.getId());

        LocalDate hoy = LocalDate.now();
        boolean tieneAcceso = false;

        // recorremos los pagos para ver si alguno esta vigente
        for (Membresia mem : membresias) {
            if (!mem.getFechaVencimiento().isBefore(hoy)) {
                tieneAcceso = true;
                break;
            }
        }

        // semaforizacion dinamica y baja automatica
        if (tieneAcceso) {
            acceso.setEsExitoso(true);
            respuesta.setEsExitoso(true);
            respuesta.setMensajeSemaforo("luz verde: acceso permitido, pago vigente");

            // aseguramos que el estado refleje que esta activo
            if (!"activo".equals(socio.getEstado())) {
                socioService.cambiarEstado(socio.getId(), "activo");
            }
        } else {
            acceso.setEsExitoso(false);
            respuesta.setEsExitoso(false);
            respuesta.setMensajeSemaforo("luz roja: membresia vencida o no encontrada");

            // automatizacion: damos de baja logica al socio
            if (!"inactivo".equals(socio.getEstado())) {
                socioService.cambiarEstado(socio.getId(), "inactivo");
            }
        }

        // guardamos el historial en base de datos
        Acceso accesoGuardado = accesoRepository.save(acceso);
        respuesta.setId(accesoGuardado.getId());

        return respuesta;
    }
}