package com.snzalx.gym.api.repository;

import com.snzalx.gym.api.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SocioRepository extends JpaRepository<Socio, UUID> {
    Optional<Socio> findByQrToken(UUID qrToken);
    Optional<Socio> findByTelefono(String telefono);

    // Este es el que usaremos para filtrar por Activos/Vencidos
    List<Socio> findByEstadoIgnoreCase(String estado);

    Long countByEstadoIgnoreCase(String estado);
    Optional<Socio> findByEmail(String email);

    @Query("SELECT s.estado, COUNT(s) FROM Socio s GROUP BY s.estado")
    List<Object[]> countAllSociosByEstado();
}