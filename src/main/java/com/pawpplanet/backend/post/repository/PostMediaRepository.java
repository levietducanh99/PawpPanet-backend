package com.pawpplanet.backend.post.repository;

import com.pawpplanet.backend.post.entity.PostMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface PostMediaRepository extends JpaRepository<PostMediaEntity, UUID> {
    List<PostMediaEntity> findByPostId(UUID postId);
}

