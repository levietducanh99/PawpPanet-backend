package com.pawpplanet.backend.post.service;

import com.pawpplanet.backend.post.dto.CommentRequest;
import com.pawpplanet.backend.post.dto.CommentResponse;

public interface CommentService {
    CommentResponse createComment(CommentRequest request);
}
