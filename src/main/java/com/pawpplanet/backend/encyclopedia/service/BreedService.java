package com.pawpplanet.backend.encyclopedia.service;

import com.pawpplanet.backend.common.dto.PagedResult;
import com.pawpplanet.backend.encyclopedia.dto.BreedResponse;
import com.pawpplanet.backend.encyclopedia.entity.BreedEntity;
import com.pawpplanet.backend.encyclopedia.repository.BreedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BreedService {

    private final BreedRepository breedRepository;

    public PagedResult<BreedResponse> getBreeds(int page, int size, Long speciesId, String taxonomyType) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<BreedEntity> p;
        if (speciesId != null && taxonomyType != null) {
            p = breedRepository.findBySpeciesIdAndTaxonomyType(speciesId, taxonomyType, pageable);
        } else if (speciesId != null) {
            p = breedRepository.findBySpeciesId(speciesId, pageable);
        } else if (taxonomyType != null) {
            p = breedRepository.findByTaxonomyType(taxonomyType, pageable);
        } else {
            p = breedRepository.findAll(pageable);
        }

        PagedResult<BreedResponse> result = new PagedResult<>();
        result.setItems(p.getContent().stream().map(this::toDto).collect(Collectors.toList()));
        result.setTotalElements(p.getTotalElements());
        result.setPage(p.getNumber());
        result.setSize(p.getSize());
        return result;
    }

    public List<BreedResponse> getBySpeciesId(Long speciesId) {
        return breedRepository.findBySpeciesId(speciesId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<BreedResponse> getById(Long id) {
        return breedRepository.findById(id).map(this::toDto);
    }

    private BreedResponse toDto(BreedEntity e) {
        BreedResponse r = new BreedResponse();
        r.setId(e.getId());
        r.setSpeciesId(e.getSpeciesId());
        r.setName(e.getName());
        r.setOrigin(e.getOrigin());
        r.setShortDescription(e.getShortDescription());
        r.setTaxonomyType(e.getTaxonomyType());
        return r;
    }
}

