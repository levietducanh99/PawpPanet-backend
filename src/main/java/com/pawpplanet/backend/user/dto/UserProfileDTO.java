package com.pawpplanet.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String email;
    private String username;
    private String fullName;
    private String role;
    private String avatarUrl;
    private String coverImageUrl;
    private String bio;
    private Boolean isVerified;
    private Instant createdAt;
    private Long followersCount;
    private Long followingCount;
    private Long petsCount;

    // Computed fields (only when viewing another user's profile)
    private Boolean isMe;           // Current user is viewing their own profile
    private Boolean isFollowing;    // Current user is following this user
    private Boolean isFollowedBy;   // This user is following current user (follower của mình)
    private Boolean canFollow;      // Current user can follow this user
}
