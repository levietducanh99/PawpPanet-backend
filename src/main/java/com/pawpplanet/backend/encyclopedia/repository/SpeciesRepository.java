package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.SpeciesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SpeciesRepository extends JpaRepository<SpeciesEntity, Long> {
    Page<SpeciesEntity> findByClassId(Long classId, Pageable pageable);

    // Non-paged finder for listing all species under a class
    List<SpeciesEntity> findByClassId(Long classId);
}