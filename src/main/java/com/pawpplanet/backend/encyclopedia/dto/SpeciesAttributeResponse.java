package com.pawpplanet.backend.encyclopedia.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SpeciesAttributeResponse {
    private Long id;
    private Long speciesId;
    private String key;
    private BigDecimal valueMin;
    private BigDecimal valueMax;
    private String unit;
    private Integer displayOrder;
}

