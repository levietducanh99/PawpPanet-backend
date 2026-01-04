package com.pawpplanet.backend.pet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for adding media to pet gallery
 * Used after frontend has successfully uploaded to Cloudinary
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddPetMediaRequest {
    /**
     * List of media items to add to the gallery
     */
    @NotNull(message = "Media list cannot be null")
    private List<MediaItem> mediaItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MediaItem {
        /**
         * Public ID of the media from Cloudinary upload response
         * Example: "pawplanet/pets/123/gallery/photo_abc123"
         */
        @NotBlank(message = "Public ID is required")
        private String publicId;

        /**
         * Type of media: image or video
         */
        @NotBlank(message = "Type is required")
        @Pattern(regexp = "^(image|video)$", message = "Type must be 'image' or 'video'")
        private String type;
    }
}
