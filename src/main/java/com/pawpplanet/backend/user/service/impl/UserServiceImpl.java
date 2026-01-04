package com.pawpplanet.backend.user.service.impl;

import com.pawpplanet.backend.media.service.CloudinaryUrlBuilder;
import com.pawpplanet.backend.pet.repository.PetRepository;
import com.pawpplanet.backend.user.dto.UpdateProfileRequestDTO;
import com.pawpplanet.backend.user.dto.UserProfileDTO;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.mapper.UserMapper;
import com.pawpplanet.backend.user.repository.FollowUserRepository;
import com.pawpplanet.backend.user.repository.UserRepository;
import com.pawpplanet.backend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FollowUserRepository followUserRepository;
    private final PetRepository petRepository;
    private final CloudinaryUrlBuilder cloudinaryUrlBuilder;

    public UserServiceImpl(
            UserRepository userRepository,
            FollowUserRepository followUserRepository,
            PetRepository petRepository,
            CloudinaryUrlBuilder cloudinaryUrlBuilder
    ) {
        this.userRepository = userRepository;
        this.followUserRepository = followUserRepository;
        this.petRepository = petRepository;
        this.cloudinaryUrlBuilder = cloudinaryUrlBuilder;
    }

    @Override
    public UserProfileDTO viewProfile() {
//        String email = "user1@example.com"; // TODO: lấy từ JWT
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return buildUserProfileDTO(user);
    }

    @Override
    public UserProfileDTO updateMyInformation(UpdateProfileRequestDTO request) {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Update full name
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        // Update avatar with publicId (builder validates internally)
        if (request.getAvatarPublicId() != null && !request.getAvatarPublicId().isBlank()) {
            user.setAvatarPublicId(request.getAvatarPublicId());
            user.setAvatarUrl(cloudinaryUrlBuilder.buildOptimizedUrl(
                    request.getAvatarPublicId(),
                    "image"
            ));
        }

        // Update cover image with publicId (builder validates internally)
        if (request.getCoverImagePublicId() != null && !request.getCoverImagePublicId().isBlank()) {
            user.setCoverImagePublicId(request.getCoverImagePublicId());
            user.setCoverImageUrl(cloudinaryUrlBuilder.buildOptimizedUrl(
                    request.getCoverImagePublicId(),
                    "image"
            ));
        }

        // Update bio
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        UserEntity saved = userRepository.save(user);
        return buildUserProfileDTO(saved);
    }
    @Override
    public UserProfileDTO getUserProfileById(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        ));

        return buildUserProfileDTO(user);
    }


    private UserProfileDTO buildUserProfileDTO(UserEntity user) {
        long followers = followUserRepository.countByIdFollowingId(user.getId());
        long following = followUserRepository.countByIdFollowerId(user.getId());
        long pets = petRepository.countByOwnerId(user.getId());

        UserProfileDTO dto = UserMapper.toUserProfileDTO(
                user,
                followers,
                following,
                pets
        );

        // Add computed fields for follow relationships
        Long currentUserId = getCurrentUserIdOrNull();
        if (currentUserId != null) {
            // Check if viewing own profile
            dto.setIsMe(currentUserId.equals(user.getId()));

            // Check if current user is following this user
            dto.setIsFollowing(followUserRepository.existsById(
                    new com.pawpplanet.backend.user.entity.FollowUserId(currentUserId, user.getId())
            ));

            // Check if this user is following current user (is this user my follower?)
            dto.setIsFollowedBy(followUserRepository.existsById(
                    new com.pawpplanet.backend.user.entity.FollowUserId(user.getId(), currentUserId)
            ));

            // Can follow if: not me AND not already following
            dto.setCanFollow(!dto.getIsMe() && !dto.getIsFollowing());
        } else {
            // Not authenticated
            dto.setIsMe(false);
            dto.setIsFollowing(false);
            dto.setIsFollowedBy(false);
            dto.setCanFollow(false);
        }

        return dto;
    }

    private Long getCurrentUserIdOrNull() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }

            String email = authentication.getName();
            if (email == null || "anonymousUser".equals(email)) {
                return null;
            }

            return userRepository.findByEmail(email)
                    .map(UserEntity::getId)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
