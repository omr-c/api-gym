package com.snzalx.gym.api.repository;

import com.snzalx.gym.api.model.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, UUID> {
    List<Membresia> findBySocioId(UUID socioId);
    List<Membresia> findByFechaVencimientoBetween(LocalDate start, LocalDate end);
    List<Membresia> findByFechaInicioBetween(LocalDate start, LocalDate end);

    @Query("SELECT m FROM Membresia m WHERE m.socio.id = :socioId ORDER BY m.fechaVencimiento DESC")
    List<Membresia> findLatestMembresiaBySocioId(UUID socioId);

    List<Membresia> findByFechaVencimiento(LocalDate fechaVencimiento);

    // Para la lista de cobros recientes (ahora con soporte de ordenación por fecha)
    List<Membresia> findTop10ByOrderByFechaInicioDesc();

    // NUEVO: Consulta para el historial mensual (Suma montos agrupados por mes/año)
    @Query(value = "SELECT TO_CHAR(fecha_inicio, 'MM') as mes, " +
            "TO_CHAR(fecha_inicio, 'YYYY') as anio, " +
            "SUM(monto) as total " +
            "FROM membresias " +
            "GROUP BY anio, mes " +
            "ORDER BY anio DESC, mes DESC " +
            "LIMIT 6", nativeQuery = true)
    List<Object[]> getHistorialMensualCifras();
}