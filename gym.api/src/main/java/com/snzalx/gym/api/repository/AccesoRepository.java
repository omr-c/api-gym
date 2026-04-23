package com.snzalx.gym.api.repository;

import com.snzalx.gym.api.model.Acceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccesoRepository extends JpaRepository<Acceso, UUID> {
    List<Acceso> findBySocioId(UUID socioId);
}
