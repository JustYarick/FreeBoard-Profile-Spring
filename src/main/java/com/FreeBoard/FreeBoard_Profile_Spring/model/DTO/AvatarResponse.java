package com.FreeBoard.FreeBoard_Profile_Spring.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvatarResponse {
    private String avatarUrl;
}
