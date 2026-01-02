package com.pawpplanet.backend.encyclopedia.controller;

import com.pawpplanet.backend.common.dto.ApiResponse;
import com.pawpplanet.backend.common.dto.PagedResult;
import com.pawpplanet.backend.encyclopedia.dto.EncyclopediaMediaResponse;
import com.pawpplanet.backend.encyclopedia.service.EncyclopediaMediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/encyclopedia")
@RequiredArgsConstructor
@Tag(name = "Encyclopedia - Media", description = "API quản lý media của encyclopedia")
public class EncyclopediaMediaController {

    private final EncyclopediaMediaService mediaService;

    @GetMapping("/species/{speciesId}/gallery")
    @Operation(summary = "Lấy gallery của species với phân trang")
    public ResponseEntity<ApiResponse<PagedResult<EncyclopediaMediaResponse>>> getSpeciesGallery(
            @PathVariable Long speciesId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        ApiResponse<PagedResult<EncyclopediaMediaResponse>> resp = new ApiResponse<>();
        resp.setResult(mediaService.getGallery("species", speciesId, page, size));
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/breeds/{breedId}/gallery")
    @Operation(summary = "Lấy gallery của breed với phân trang")
    public ResponseEntity<ApiResponse<PagedResult<EncyclopediaMediaResponse>>> getBreedGallery(
            @PathVariable Long breedId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        ApiResponse<PagedResult<EncyclopediaMediaResponse>> resp = new ApiResponse<>();
        resp.setResult(mediaService.getGallery("breed", breedId, page, size));
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/classes/{classId}/gallery")
    @Operation(summary = "Lấy gallery của class với phân trang")
    public ResponseEntity<ApiResponse<PagedResult<EncyclopediaMediaResponse>>> getClassGallery(
            @PathVariable Long classId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        ApiResponse<PagedResult<EncyclopediaMediaResponse>> resp = new ApiResponse<>();
        resp.setResult(mediaService.getGallery("class", classId, page, size));
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }
}

