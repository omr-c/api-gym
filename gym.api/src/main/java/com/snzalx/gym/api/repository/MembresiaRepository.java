package com.snzalx.gym.api.repository;

import com.snzalx.gym.api.model.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, UUID> {
    List<Membresia> findBySocioId(UUID socioId);
}
