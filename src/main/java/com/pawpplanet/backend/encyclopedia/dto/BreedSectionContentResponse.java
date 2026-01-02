package com.pawpplanet.backend.encyclopedia.dto;

import lombok.Data;

@Data
public class BreedSectionContentResponse {
    private Long id;
    private Long breedId;
    private Long sectionId;
    private String sectionCode;
    private String sectionName;
    private String language;
    private String content;
    private Integer displayOrder;
}

