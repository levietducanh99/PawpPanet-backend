package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.SpeciesSectionContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeciesSectionContentRepository extends JpaRepository<SpeciesSectionContentEntity, Long> {

    @Query("SELECT ssc FROM SpeciesSectionContentEntity ssc LEFT JOIN FETCH ssc.section WHERE ssc.speciesId = :speciesId ORDER BY ssc.displayOrder ASC")
    List<SpeciesSectionContentEntity> findBySpeciesIdOrderByDisplayOrderAsc(@Param("speciesId") Long speciesId);

    List<SpeciesSectionContentEntity> findBySectionId(Long sectionId);
}

