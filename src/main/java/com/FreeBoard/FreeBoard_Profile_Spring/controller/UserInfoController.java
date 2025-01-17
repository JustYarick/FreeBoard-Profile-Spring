package com.FreeBoard.FreeBoard_Profile_Spring.controller;


import com.FreeBoard.FreeBoard_Profile_Spring.service.S3Service;
import com.FreeBoard.FreeBoard_Profile_Spring.service.SecurityService;
import com.FreeBoard.FreeBoard_Profile_Spring.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

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
            return ResponseEntity.ok(userInfoService.GetProfileUser(securityService.getCurrentUserEmail()));
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
        String userEmail = securityService.getCurrentUserEmail();
        if(userEmail != null) {
            try {

                String fileName = UUID.randomUUID().toString() + ".jpg";
                String avatarUrl = s3Service.uploadFile(file, fileName);

                userInfoService.UpdateAvatarUrl(userEmail, avatarUrl);
                return ResponseEntity.ok().build();

            } catch ( Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.badRequest().body("Invalid user");
    }

    @GetMapping("/demo")
    public ResponseEntity<String> demo() {
        return ResponseEntity.ok("Hello World");
    }
}
