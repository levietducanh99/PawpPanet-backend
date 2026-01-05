package com.pawpplanet.backend.post.controller;

import com.pawpplanet.backend.post.dto.CommentRequest;
import com.pawpplanet.backend.post.dto.CommentResponse;
import com.pawpplanet.backend.post.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentResponse createComment(@RequestBody @Valid CommentRequest request) {
        return commentService.createComment(request);
    }
}
