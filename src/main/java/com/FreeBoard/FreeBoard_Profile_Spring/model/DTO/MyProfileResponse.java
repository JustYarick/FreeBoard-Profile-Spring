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
public class MyProfileResponse {
    private String avatarUrl;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MyProfileResponse convertToMyProfileResponse(ProfileUserEntity entity) {
        return MyProfileResponse.builder()
                .avatarUrl(entity.getAvatarUrl())
                .bio(entity.getBio())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}