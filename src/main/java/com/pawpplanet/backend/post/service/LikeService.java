package com.pawpplanet.backend.post.service;

import com.pawpplanet.backend.post.dto.LikeRequest;
import com.pawpplanet.backend.post.dto.LikeResponse;

public interface LikeService {

    LikeResponse toggleLike(LikeRequest request);

}

