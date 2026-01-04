package com.pawpplanet.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequestDTO {
    private String fullName;  // Tên đầy đủ

    private String avatarPublicId;  // Cloudinary public_id for avatar

    private String coverImagePublicId;  // Cloudinary public_id for cover image

    private String bio;

}
