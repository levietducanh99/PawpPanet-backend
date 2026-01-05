package com.pawpplanet.backend.post.controller;

import com.pawpplanet.backend.post.dto.LikeRequest;
import com.pawpplanet.backend.post.dto.LikeResponse;
import com.pawpplanet.backend.post.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping()
    public LikeResponse toggleLike(@RequestBody LikeRequest request) {
        return likeService.toggleLike(request);
    }
}

