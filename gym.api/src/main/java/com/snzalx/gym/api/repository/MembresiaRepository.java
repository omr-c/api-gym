package com.snzalx.gym.api.repository;

import com.snzalx.gym.api.model.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, UUID> {
    List<Membresia> findBySocioId(UUID socioId);
    
    // Busca membresías que vencen entre dos fechas
    List<Membresia> findByFechaVencimientoBetween(LocalDate start, LocalDate end);

    // Para el dashboard, calcular ingresos en un rango de fechas de inicio de pago
    List<Membresia> findByFechaInicioBetween(LocalDate start, LocalDate end);

    // Nuevo: Obtener la última membresía activa de un socio
    @Query("SELECT m FROM Membresia m WHERE m.socio.id = :socioId ORDER BY m.fechaVencimiento DESC")
    List<Membresia> findLatestMembresiaBySocioId(UUID socioId);

    // Nuevo: Buscar socios cuya membresía vence en una fecha específica
    List<Membresia> findByFechaVencimiento(LocalDate fechaVencimiento);
}
