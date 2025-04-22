package com.FreeBoard.FreeBoard_Profile_Spring.model.DTO;

import com.FreeBoard.FreeBoard_Profile_Spring.model.Entity.ProfileUserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    private String avatarUrl;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProfileDTO convert(ProfileUserEntity entity) {
        return ProfileDTO.builder()
                .avatarUrl(entity.getAvatarFileName())
                .bio(entity.getBio())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}