package com.FreeBoard.FreeBoard_Profile_Spring.controller;


import com.FreeBoard.FreeBoard_Profile_Spring.Entity.ProfileUserEntity;
import com.FreeBoard.FreeBoard_Profile_Spring.model.MyProfileResponse;
import com.FreeBoard.FreeBoard_Profile_Spring.service.S3Service;
import com.FreeBoard.FreeBoard_Profile_Spring.service.SecurityService;
import com.FreeBoard.FreeBoard_Profile_Spring.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;
    private final S3Service s3Service;
    private final SecurityService securityService;

    @GetMapping("/me")
    public ResponseEntity<?> getMe() {
        try {
            ProfileUserEntity profileUserEntity = userInfoService.GetProfileUser(SecurityService.getCurrentUser())
                    .orElseThrow(() -> new Exception("User not found"));
            return ResponseEntity.ok(MyProfileResponse.convertToMyProfileResponse(profileUserEntity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Метод для обновления аватара пользователя.
     * @param file файл с аватаром.
     * @return ответ с URL нового аватара.
     */
    @PostMapping("/updateAvatar")
    public ResponseEntity<?> updateAvatar(@RequestParam("avatar") MultipartFile file) {
        UUID user_id = securityService.getCurrentUser();
        if (user_id != null) {
            try {
                String avatarUrl = userInfoService.updateAvatar(user_id, file);
                Map<String, String> response = new HashMap<>();
                response.put("avatarUrl", avatarUrl);
                return ResponseEntity.ok(response);

            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.badRequest().body("Invalid user");
    }


    @GetMapping("/demo")
    public ResponseEntity<Map<String, String>> demo() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello World");
        return ResponseEntity.ok(response);
    }
}
