package com.FreeBoard.FreeBoard_Profile_Spring.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvatarResponse {
    public AvatarResponse(String avatar) {
        this.avatarFileName = avatar;
    }
    private String avatarFileName;
    @Builder.Default
    private Instant timestamp = Instant.now();
}
