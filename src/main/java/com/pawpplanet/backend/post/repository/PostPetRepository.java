package com.pawpplanet.backend.post.repository;

import com.pawpplanet.backend.post.entity.PostPetEntity;
import com.pawpplanet.backend.post.entity.PostPetId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostPetRepository extends JpaRepository<PostPetEntity, PostPetId> {
    List<PostPetEntity> findByIdPostId(java.util.UUID postId);
    List<PostPetEntity> findByIdPetId(java.util.UUID petId);
}

