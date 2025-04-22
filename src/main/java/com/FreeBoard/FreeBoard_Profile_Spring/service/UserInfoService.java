package com.FreeBoard.FreeBoard_Profile_Spring.service;

import com.FreeBoard.FreeBoard_Profile_Spring.exception.UserNotFoundException;
import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.ProfileCreatedEventDTO;
import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.ProfileFailedEventDTO;
import com.FreeBoard.FreeBoard_Profile_Spring.model.Entity.ProfileUserEntity;
import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.NewUserEventDTO;
import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.UpdaeInfoRequest;
import com.FreeBoard.FreeBoard_Profile_Spring.repository.ProfileUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoService {

    private final ProfileUserRepository profileUserRepository;
    private final S3Service s3Service;
    private final KafkaService kafkaService;

    public void createNewProfile(NewUserEventDTO newUserEvent) {
        try {
            ProfileUserEntity newUserProfile = new ProfileUserEntity(newUserEvent.getUserId());
            profileUserRepository.save(newUserProfile);
            kafkaService.sendProfileCreatedEvent(
                    ProfileCreatedEventDTO.builder()
                            .userId(newUserEvent.getUserId())
                            .sagaId(newUserEvent.getSagaId())
                            .build()
            );
        } catch (Exception e) {
            log.error("Error creating user profile for {}: {}", newUserEvent.getUserId(), e.getMessage());

            kafkaService.sendProfileFailedEvent(
                    ProfileFailedEventDTO.builder()
                    .sagaId(newUserEvent.getSagaId())
                    .userId(newUserEvent.getUserId())
                    .error(e.getMessage())
                    .build()
            );
        }
    }


    public ProfileUserEntity getCurrentUserProfile() {
        return profileUserRepository.findByUserId(
                SecurityService.getCurrentUser()
        ).orElseThrow(() -> new UserNotFoundException("Invalid user data"));
    }

    public String updateCurrentUserAvatar(MultipartFile file) throws IOException {
        UUID userId = SecurityService.getCurrentUser();
        return updateAvatar(userId, file);
    }

    public String updateAvatar(UUID userId, MultipartFile file) throws IOException {
        ProfileUserEntity profileUser = profileUserRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        String newAvatar;
        if (profileUser.getAvatarFileName() != null && !profileUser.getAvatarFileName().isEmpty()) {
            newAvatar = s3Service.updateFile(profileUser.getAvatarFileName(), file);
            profileUser.setAvatarFileName(newAvatar);
            profileUserRepository.save(profileUser);
            return newAvatar;
        } else {
            String fileName = UUID.randomUUID().toString() + ".jpg";
            newAvatar = s3Service.uploadFile(file, fileName);

            profileUser.setAvatarFileName(newAvatar);
            profileUserRepository.save(profileUser);
        }

        return newAvatar;
    }

    public ProfileUserEntity updateInfo(UpdaeInfoRequest updaeInfoRequest) {
        UUID userId = SecurityService.getCurrentUser();
        ProfileUserEntity profileUser = profileUserRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        profileUser.setBio(updaeInfoRequest.getBio());
        return profileUserRepository.save(profileUser);
    }
}
