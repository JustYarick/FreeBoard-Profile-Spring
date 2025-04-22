package com.FreeBoard.FreeBoard_Profile_Spring.controller;


import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.AvatarResponse;
import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.ProfileDTO;
import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.UpdaeInfoRequest;
import com.FreeBoard.FreeBoard_Profile_Spring.service.UserInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping("/me")
    public ResponseEntity<ProfileDTO> getMe() {
        return ResponseEntity.ok(
                ProfileDTO.convert(userInfoService.getCurrentUserProfile()
                )
        );
    }

    @PostMapping("/updateAvatar")
    public ResponseEntity<AvatarResponse> updateAvatar(@RequestParam("avatar") MultipartFile file) throws IOException {
        return ResponseEntity.ok(
                new AvatarResponse(
                        userInfoService.updateCurrentUserAvatar(file)
                )
        );
    }

    @PostMapping("/updateMe")
    public ResponseEntity<ProfileDTO> updateInfo(@Valid @RequestBody UpdaeInfoRequest updateInfoRequest) {
        return ResponseEntity.ok(
                ProfileDTO.convert(userInfoService.updateInfo(updateInfoRequest)
                )
        );
    }
}