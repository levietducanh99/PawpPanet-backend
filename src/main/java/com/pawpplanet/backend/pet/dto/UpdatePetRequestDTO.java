package com.pawpplanet.backend.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePetRequestDTO {
    private String name;
    private Long speciesId;
    private Long breedId;
    private LocalDate birthDate;
    private String gender;
    private String description;
    private String status;
    private String avatarPublicId;  // Cloudinary public_id for avatar
    private BigDecimal weight;

    private BigDecimal height;
}
