package com.pawpplanet.backend.media.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for building Cloudinary URLs from public IDs
 * Supports transformations for optimization and resizing
 */
@Service
@RequiredArgsConstructor
public class CloudinaryUrlBuilder {

    private final Cloudinary cloudinary;

    /**
     * Build a basic image URL from public ID
     *
     * @param publicId The Cloudinary public ID
     * @return Full HTTPS URL to the image
     */
    public String buildImageUrl(String publicId) {
        return cloudinary.url()
                .secure(true)
                .resourceType("image")
                .generate(publicId);
    }

    /**
     * Build a basic video URL from public ID
     *
     * @param publicId The Cloudinary public ID
     * @return Full HTTPS URL to the video
     */
    public String buildVideoUrl(String publicId) {
        return cloudinary.url()
                .secure(true)
                .resourceType("video")
                .generate(publicId);
    }

    /**
     * Build URL with automatic format optimization
     *
     * @param publicId The Cloudinary public ID
     * @param resourceType "image" or "video"
     * @return Optimized URL
     */
    public String buildOptimizedUrl(String publicId, String resourceType) {
        return cloudinary.url()
                .secure(true)
                .resourceType(resourceType)
                .transformation(new Transformation().quality("auto").fetchFormat("auto"))
                .generate(publicId);
    }

    /**
     * Build thumbnail URL (for image only)
     *
     * @param publicId The Cloudinary public ID
     * @param width Thumbnail width
     * @param height Thumbnail height
     * @return Thumbnail URL
     */
    public String buildThumbnailUrl(String publicId, int width, int height) {
        return cloudinary.url()
                .secure(true)
                .resourceType("image")
                .transformation(new Transformation()
                        .width(width)
                        .height(height)
                        .crop("fill")
                        .quality("auto")
                        .fetchFormat("auto"))
                .generate(publicId);
    }

    /**
     * Validate that public ID belongs to the expected folder
     *
     * @param publicId The Cloudinary public ID
     * @param expectedFolder Expected folder path (e.g., "pawplanet/pets/123/gallery")
     * @return true if valid, false otherwise
     */
    public boolean validatePublicId(String publicId, String expectedFolder) {
        if (publicId == null || publicId.isEmpty()) {
            return false;
        }

        // Public ID should start with the expected folder
        return publicId.startsWith(expectedFolder + "/");
    }

    /**
     * Extract folder from public ID
     * Example: "pawplanet/pets/123/gallery/photo_abc" -> "pawplanet/pets/123/gallery"
     *
     * @param publicId The Cloudinary public ID
     * @return Folder path
     */
    public String extractFolder(String publicId) {
        if (publicId == null || !publicId.contains("/")) {
            return "";
        }

        int lastSlash = publicId.lastIndexOf('/');
        return publicId.substring(0, lastSlash);
    }

    /**
     * Get expected folder for pet gallery
     *
     * @param petId Pet ID
     * @return Expected folder path
     */
    public String getExpectedPetGalleryFolder(Long petId) {
        return String.format("pawplanet/pets/%d/gallery", petId);
    }

    /**
     * Get expected folder for pet avatar
     *
     * @param petId Pet ID
     * @return Expected folder path
     */
    public String getExpectedPetAvatarFolder(Long petId) {
        return String.format("pawplanet/pets/%d/avatar", petId);
    }
}

