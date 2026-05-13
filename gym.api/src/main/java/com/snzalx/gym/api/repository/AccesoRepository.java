package com.snzalx.gym.api.repository;

import com.snzalx.gym.api.model.Acceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccesoRepository extends JpaRepository<Acceso, UUID> {
    long countByFechaAccesoBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<Acceso> findByFechaAccesoBetween(LocalDateTime start, LocalDateTime end);
    List<Acceso> findTop10ByOrderByFechaAccesoDesc();


    @Query(value = "SELECT TO_CHAR(a.fecha_acceso, 'YYYY-MM-DD') as etiqueta, COUNT(a.id) as conteo " +
            "FROM accesos a " +
            "WHERE a.fecha_acceso BETWEEN :startDate AND :endDate " +
            "GROUP BY etiqueta ORDER BY etiqueta", nativeQuery = true)
    List<Object[]> countAccessesByDay(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}