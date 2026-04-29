package com.snzalx.gym.api.repository;

import com.snzalx.gym.api.model.Acceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccesoRepository extends JpaRepository<Acceso, UUID> {
    List<Acceso> findBySocioId(UUID socioId);
    
    // Nuevo: Obtiene los últimos accesos registrados ordenados por fecha de forma descendente
    List<Acceso> findTop10ByOrderByFechaHoraDesc();

    // Nuevo: Para el dashboard, contar accesos en un rango de fechas
    List<Acceso> findByFechaHoraBetween(LocalDateTime start, LocalDateTime end);
}
