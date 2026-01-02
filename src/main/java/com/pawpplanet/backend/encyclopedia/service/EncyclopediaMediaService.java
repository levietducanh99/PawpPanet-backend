package com.pawpplanet.backend.encyclopedia.service;

import com.pawpplanet.backend.common.dto.PagedResult;
import com.pawpplanet.backend.encyclopedia.dto.EncyclopediaMediaResponse;
import com.pawpplanet.backend.encyclopedia.entity.EncyclopediaMediaEntity;
import com.pawpplanet.backend.encyclopedia.repository.EncyclopediaMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EncyclopediaMediaService {

    private final EncyclopediaMediaRepository mediaRepository;

    /**
     * Lấy thumbnail (avatar) cho entity
     */
    public String getThumbnailUrl(String entityType, Long entityId) {
        return mediaRepository.findFirstByEntityTypeAndEntityIdAndRole(entityType, entityId, "thumbnail")
                .map(EncyclopediaMediaEntity::getUrl)
                .orElse(null);
    }

    /**
     * Lấy hero banner cho entity
     */
    public String getHeroUrl(String entityType, Long entityId) {
        return mediaRepository.findFirstByEntityTypeAndEntityIdAndRole(entityType, entityId, "hero")
                .map(EncyclopediaMediaEntity::getUrl)
                .orElse(null);
    }

    /**
     * Lấy gallery preview (vài ảnh đầu tiên) cho detail page
     */
    public List<EncyclopediaMediaResponse> getGalleryPreview(String entityType, Long entityId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return mediaRepository.findTopGalleryByEntityTypeAndEntityId(entityType, entityId, pageable)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả gallery với pagination
     */
    public PagedResult<EncyclopediaMediaResponse> getGallery(String entityType, Long entityId, int page, int size) {
        // Convert 1-based page to 0-based
        int zeroBasedPage = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(zeroBasedPage, Math.max(1, size));

        Page<EncyclopediaMediaEntity> p = mediaRepository.findGalleryByEntityTypeAndEntityId(
                entityType, entityId, pageable);

        PagedResult<EncyclopediaMediaResponse> result = new PagedResult<>();
        result.setItems(p.getContent().stream().map(this::toDto).collect(Collectors.toList()));
        result.setTotalElements(p.getTotalElements());
        result.setPage(p.getNumber() + 1);  // Convert back to 1-based
        result.setSize(p.getSize());
        return result;
    }

    private EncyclopediaMediaResponse toDto(EncyclopediaMediaEntity e) {
        EncyclopediaMediaResponse r = new EncyclopediaMediaResponse();
        r.setId(e.getId());
        r.setEntityType(e.getEntityType());
        r.setEntityId(e.getEntityId());
        r.setType(e.getType());
        r.setRole(e.getRole());
        r.setUrl(e.getUrl());
        r.setDisplayOrder(e.getDisplayOrder());
        return r;
    }
}

