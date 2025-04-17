package com.FreeBoard.FreeBoard_Profile_Spring.controller;


import com.FreeBoard.FreeBoard_Profile_Spring.model.Entity.ProfileUserEntity;
import com.FreeBoard.FreeBoard_Profile_Spring.exception.UserNotFoundException;
import com.FreeBoard.FreeBoard_Profile_Spring.model.AvatarResponse;
import com.FreeBoard.FreeBoard_Profile_Spring.model.MyProfileResponse;
import com.FreeBoard.FreeBoard_Profile_Spring.model.UpdaeInfoRequest;
import com.FreeBoard.FreeBoard_Profile_Spring.service.SecurityService;
import com.FreeBoard.FreeBoard_Profile_Spring.service.UserInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping("/me")
    public ResponseEntity<MyProfileResponse> getMe() {
        ProfileUserEntity profileUserEntity = userInfoService
                .GetProfileUser(SecurityService.getCurrentUser())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return ResponseEntity.ok(MyProfileResponse.convertToMyProfileResponse(profileUserEntity));
    }

    @PostMapping("/updateAvatar")
    public ResponseEntity<AvatarResponse> updateAvatar(@RequestParam("avatar") MultipartFile file) throws IOException {
        UUID userId = SecurityService.getCurrentUser();
        String avatarUrl = userInfoService.updateAvatar(userId, file);
        return ResponseEntity.ok(new AvatarResponse(avatarUrl));
    }

    @PostMapping("/updateMe")
    public ResponseEntity<Void> updateInfo(@Valid @RequestBody UpdaeInfoRequest updateInfoRequest) {
        userInfoService.updateInfo(updateInfoRequest);
        return ResponseEntity.ok().build();
    }
}