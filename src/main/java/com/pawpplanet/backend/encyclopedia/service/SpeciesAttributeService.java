package com.pawpplanet.backend.encyclopedia.service;

import com.pawpplanet.backend.encyclopedia.dto.SpeciesAttributeResponse;
import com.pawpplanet.backend.encyclopedia.entity.SpeciesAttributeEntity;
import com.pawpplanet.backend.encyclopedia.repository.SpeciesAttributeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpeciesAttributeService {

    private final SpeciesAttributeRepository repository;

    public List<SpeciesAttributeResponse> getAllAttributes(Long speciesId) {
        return repository.findBySpeciesIdOrderByDisplayOrderAsc(speciesId).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    private SpeciesAttributeResponse toDto(SpeciesAttributeEntity e) {
        SpeciesAttributeResponse r = new SpeciesAttributeResponse();
        r.setId(e.getId());
        r.setSpeciesId(e.getSpeciesId());
        r.setKey(e.getKey());
        r.setValueMin(e.getValueMin());
        r.setValueMax(e.getValueMax());
        r.setUnit(e.getUnit());
        r.setDisplayOrder(e.getDisplayOrder());
        return r;
    }
}
