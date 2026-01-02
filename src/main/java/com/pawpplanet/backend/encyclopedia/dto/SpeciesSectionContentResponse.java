package com.pawpplanet.backend.encyclopedia.dto;

import lombok.Data;

@Data
public class SpeciesSectionContentResponse {
    private Long id;
    private Long speciesId;
    private Long sectionId;
    private String sectionCode;
    private String sectionName;
    private String language;
    private String content;
    private Integer displayOrder;
}

