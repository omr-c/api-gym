package com.snzalx.gym.api.service;

import com.snzalx.gym.api.dto.SocioDTO;
import com.snzalx.gym.api.model.Membresia;
import com.snzalx.gym.api.model.Socio;
import com.snzalx.gym.api.repository.MembresiaRepository;
import com.snzalx.gym.api.repository.SocioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SocioService {

    private final SocioRepository socioRepository;
    private final MembresiaRepository membresiaRepository;
    private final EmailService emailService;

    // se reutiliza la instancia de random para optimizar recursos
    private static final Random random = new Random();

    // mapa para almacenamiento temporal de codigos de verificacion
    private final Map<String, String> codigosVerificacion = new ConcurrentHashMap<>();

    public SocioService(SocioRepository socioRepository, MembresiaRepository membresiaRepository, EmailService emailService) {
        this.socioRepository = socioRepository;
        this.membresiaRepository = membresiaRepository;
        this.emailService = emailService;
    }

    // genera el codigo solo si el correo y el telefono no estan registrados
    public void solicitarCodigo(String email, String telefono) {
        log.info("buscando duplicados para email: {} o tel: {}", email, telefono);

        if (socioRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("El correo ya se encuentra registrado");
        }
        if (socioRepository.findByTelefono(telefono).isPresent()) {
            throw new IllegalArgumentException("El telefono ya se encuentra registrado");
        }

        String codigo = String.format("%06d", random.nextInt(999999));
        codigosVerificacion.put(email, codigo);

        emailService.enviarCorreoGenerico(email, "Codigo de Verificacion - Gym Rats",
                "Tu codigo para completar el registro es: " + codigo);
        log.info("codigo enviado satisfactoriamente a -> {}", email);
    }

    // comprueba que el codigo ingresado sea igual al guardado en memoria
    public boolean validarCodigo(String email, String codigo) {
        String codigoGuardado = codigosVerificacion.get(email);
        return codigoGuardado != null && codigoGuardado.equals(codigo);
    }

    // persiste al socio en la base de datos
    public Socio registrarSocio(Socio socio) {
        log.info("procesando guardado de socio -> {}", socio.getEmail());

        if (socioRepository.findByEmail(socio.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya existe");
        }

        if (socioRepository.findByTelefono(socio.getTelefono()).isPresent()) {
            throw new IllegalArgumentException("El telefono ya existe");
        }

        if (socio.getRol() == null || socio.getRol().isEmpty()) {
            socio.setRol("socio");
        }

        socio.setEstado("pendiente");
        socio.setQrToken(UUID.randomUUID());

        Socio guardado = socioRepository.save(socio);
        codigosVerificacion.remove(socio.getEmail());

        if (guardado.getEmail() != null && !guardado.getEmail().isEmpty()) {
            emailService.enviarBienvenida(guardado.getEmail(), guardado.getNombre());
        }

        return guardado;
    }

    // actualiza el estado del socio, utilizado desde MembresiaService
    public Socio cambiarEstado(UUID id, String nuevoEstado) {
        log.info("cambiando estado del socio {} a {}", id, nuevoEstado);
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("socio no encontrado"));
        socio.setEstado(nuevoEstado);
        return socioRepository.save(socio);
    }

    // obtiene la entidad completa mediante el token del qr
    public Socio obtenerPorQr(UUID qrToken) {
        return socioRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new NoSuchElementException("socio no encontrado"));
    }

    // obtiene los datos formateados para enviar a la app
    public SocioDTO obtenerSocioDtoPorQr(UUID qrToken) {
        return convertSocioToDto(obtenerPorQr(qrToken));
    }
    // Método para actualizar nombre y foto de perfil
    public Socio actualizarPerfil(UUID id, String nombre, String fotoUrl) {
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Socio no encontrado"));
        socio.setNombre(nombre);
        socio.setFotoUrl(fotoUrl);
        return socioRepository.save(socio);
    }
    // devuelve todos los socios con membresia vigente
    public List<SocioDTO> listarActivos() {
        return socioRepository.findByEstadoIgnoreCase("activo").stream()
                .map(this::convertSocioToDto)
                .toList();
    }

    // transforma la entidad socio a un objeto plano de transferencia
    private SocioDTO convertSocioToDto(Socio socio) {
        SocioDTO dto = new SocioDTO();
        dto.setId(socio.getId());
        dto.setNombre(socio.getNombre());
        dto.setTelefono(socio.getTelefono());
        dto.setEmail(socio.getEmail());
        dto.setFotoUrl(socio.getFotoUrl());
        dto.setQrToken(socio.getQrToken());
        dto.setBio(socio.getBio());
        dto.setInstagramUrl(socio.getInstagramUrl());
        dto.setEstado(socio.getEstado());

        List<Membresia> membresias = membresiaRepository.findLatestMembresiaBySocioId(socio.getId());
        if (!membresias.isEmpty()) {
            Membresia ultima = membresias.getFirst();
            LocalDate hoy = LocalDate.now();
            if (ultima.getFechaVencimiento().isAfter(hoy)) {
                dto.setDiasRestantes(ChronoUnit.DAYS.between(hoy, ultima.getFechaVencimiento()));
            } else {
                dto.setDiasRestantes(0L);
            }
        } else {
            dto.setDiasRestantes(0L);
        }

        return dto;
    }
}