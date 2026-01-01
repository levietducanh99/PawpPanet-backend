package com.pawpplanet.backend.encyclopedia.controller;

import com.pawpplanet.backend.common.dto.ApiResponse;
import com.pawpplanet.backend.encyclopedia.dto.AnimalClassResponse;
import com.pawpplanet.backend.encyclopedia.dto.SpeciesResponse;
import com.pawpplanet.backend.encyclopedia.service.AnimalClassService;
import com.pawpplanet.backend.encyclopedia.service.SpeciesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/encyclopedia/classes")
@RequiredArgsConstructor
@Tag(name = "Encyclopedia - Classes", description = "API quản lý các lớp động vật")
public class AnimalClassController {

    private final AnimalClassService service;
    private final SpeciesService speciesService;

    @GetMapping
    @Operation(summary = "Lấy danh sách các lớp động vật")
    public ResponseEntity<ApiResponse<List<AnimalClassResponse>>> listAll() {
        ApiResponse<List<AnimalClassResponse>> resp = new ApiResponse<>();
        resp.setResult(service.getAllClasses());
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin lớp động vật theo ID")
    public ResponseEntity<ApiResponse<AnimalClassResponse>> getById(@PathVariable Long id) {
        ApiResponse<AnimalClassResponse> resp = new ApiResponse<>();
        service.getById(id).ifPresent(resp::setResult);
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Lấy thông tin lớp động vật theo mã (code)")
    public ResponseEntity<ApiResponse<AnimalClassResponse>> getByCode(@PathVariable String code) {
        ApiResponse<AnimalClassResponse> resp = new ApiResponse<>();
        service.getByCode(code).ifPresent(resp::setResult);
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{classId}/species")
    @Operation(summary = "Lấy danh sách species theo classId")
    public ResponseEntity<ApiResponse<List<SpeciesResponse>>> getSpeciesByClass(@PathVariable Long classId) {
        ApiResponse<List<SpeciesResponse>> resp = new ApiResponse<>();
        resp.setResult(speciesService.getByClassId(classId));
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }
}
