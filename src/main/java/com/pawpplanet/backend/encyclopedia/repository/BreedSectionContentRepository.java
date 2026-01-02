package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.BreedSectionContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BreedSectionContentRepository extends JpaRepository<BreedSectionContentEntity, Long> {
    List<BreedSectionContentEntity> findByBreedId(Long breedId);

    @Query("SELECT bsc FROM BreedSectionContentEntity bsc LEFT JOIN FETCH bsc.section WHERE bsc.breed.id = :breedId ORDER BY bsc.displayOrder ASC")
    List<BreedSectionContentEntity> findByBreedIdOrderByDisplayOrderAsc(@Param("breedId") Long breedId);

    List<BreedSectionContentEntity> findBySectionId(Long sectionId);
}

