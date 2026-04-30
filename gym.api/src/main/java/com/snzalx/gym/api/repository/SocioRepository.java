package com.snzalx.gym.api.repository;

import com.snzalx.gym.api.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SocioRepository extends JpaRepository<Socio, UUID> {
    // busca al socio por su token de identidad digital[cite: 6]
    Optional<Socio> findByQrToken(UUID qrToken);

    // busca por numero de celular[cite: 6]
    Optional<Socio> findByTelefono(String telefono);

    // filtra socios por su estado[cite: 6]
    List<Socio> findByEstadoIgnoreCase(String estado);

    // indispensable para el inicio de sesion con google
    Optional<Socio> findByEmail(String email);
}