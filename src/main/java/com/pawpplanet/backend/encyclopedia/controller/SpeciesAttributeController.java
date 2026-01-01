package com.pawpplanet.backend.encyclopedia.controller;

import com.pawpplanet.backend.common.dto.ApiResponse;
import com.pawpplanet.backend.encyclopedia.dto.SpeciesAttributeResponse;
import com.pawpplanet.backend.encyclopedia.service.SpeciesAttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/encyclopedia/species-attributes")
@RequiredArgsConstructor
@Tag(name = "Encyclopedia - Species Attributes", description = "API quản lý các thuộc tính của species")
public class SpeciesAttributeController {

    private final SpeciesAttributeService service;

    @GetMapping("/all")
    @Operation(summary = "Lấy tất cả thuộc tính theo species (không phân trang), sắp theo displayOrder")
    public ResponseEntity<ApiResponse<List<SpeciesAttributeResponse>>> listAll(@RequestParam("speciesId") Long speciesId) {
        ApiResponse<List<SpeciesAttributeResponse>> resp = new ApiResponse<>();
        resp.setResult(service.getAllAttributes(speciesId));
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }
}
