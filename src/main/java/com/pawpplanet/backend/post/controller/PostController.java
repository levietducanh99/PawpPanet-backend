package com.pawpplanet.backend.post.controller;

import com.pawpplanet.backend.post.dto.CreatePostRequest;
import com.pawpplanet.backend.post.dto.PostResponse;
import com.pawpplanet.backend.post.dto.UpdatePostRequest;
import com.pawpplanet.backend.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;



    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody CreatePostRequest request
    ) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestBody UpdatePostRequest request
    ) {
        return ResponseEntity.ok(postService.updatePost(id, request));
    }


    @GetMapping
    public ResponseEntity<List<PostResponse>> getMyPosts() {
        return ResponseEntity.ok(postService.getMyPosts());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> getPostsByUserId(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(postService.getPostsByUserId(userId));
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<PostResponse>> getPostsByPetId(
            @PathVariable Long petId
    ) {
        return ResponseEntity.ok(postService.getPostsByPetId(petId));
    }
}