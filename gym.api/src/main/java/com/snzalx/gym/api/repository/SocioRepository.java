package com.snzalx.gym.api.repository;

import com.snzalx.gym.api.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SocioRepository extends JpaRepository<Socio, UUID> {
    Optional<Socio> findByQrToken(UUID qrToken);
    Optional<Socio> findByTelefono(String telefono);
}
