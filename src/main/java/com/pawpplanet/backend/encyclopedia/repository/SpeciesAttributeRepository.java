package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.SpeciesAttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeciesAttributeRepository extends JpaRepository<SpeciesAttributeEntity, Long> {
    List<SpeciesAttributeEntity> findBySpeciesIdOrderByDisplayOrderAsc(Long speciesId);
}
