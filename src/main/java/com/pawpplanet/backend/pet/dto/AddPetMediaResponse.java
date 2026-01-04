package com.pawpplanet.backend.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for adding media to pet gallery
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddPetMediaResponse {
    /**
     * ID of the pet
     */
    private Long petId;

    /**
     * List of media that was added
     */
    private List<PetMediaDTO> addedMedia;

    /**
     * Total count of media in the gallery after adding
     */
    private Integer totalGalleryCount;

    /**
     * Success message
     */
    private String message;
}
