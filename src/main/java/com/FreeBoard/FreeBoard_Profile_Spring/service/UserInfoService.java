package com.FreeBoard.FreeBoard_Profile_Spring.service;

import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.ProfileCreatedEventDTO;
import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.ProfileFailedEventDTO;
import com.FreeBoard.FreeBoard_Profile_Spring.model.Entity.ProfileUserEntity;
import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.NewUserEventDTO;
import com.FreeBoard.FreeBoard_Profile_Spring.model.UpdaeInfoRequest;
import com.FreeBoard.FreeBoard_Profile_Spring.repository.ProfileUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoService {

    private final ProfileUserRepository profileUserRepository;
    private final S3Service s3Service;
    private final SecurityService securityService;
    private final KafkaService kafkaService;

    public void createNewProfile(NewUserEventDTO newUserEvent) {
        try {
            var newUserProfile = ProfileUserEntity.builder()
                    .userId(newUserEvent.getUserId())
                    .email(newUserEvent.getEmail())
                    .username(newUserEvent.getUsername())
                    .build();

            profileUserRepository.save(newUserProfile);

            ProfileCreatedEventDTO createdEvent = ProfileCreatedEventDTO.builder()
                    .sagaId(newUserEvent.getSagaId())
                    .userId(newUserEvent.getUserId())
                    .timestamp(Instant.now())
                    .build();

            kafkaService.sendProfileCreatedEvent(createdEvent);

        } catch (Exception e) {
            log.error("Error creating user profile for {}: {}", newUserEvent.getUserId(), e.getMessage());

            ProfileFailedEventDTO failedEvent = ProfileFailedEventDTO.builder()
                    .sagaId(newUserEvent.getSagaId())
                    .userId(newUserEvent.getUserId())
                    .error(e.getMessage())
                    .timestamp(Instant.now())
                    .build();

            kafkaService.sendProfileFailedEvent(failedEvent);
        }
    }


    public Optional<ProfileUserEntity> GetProfileUser(UUID user_id) {
        return profileUserRepository.findByUserId(user_id);
    }

    public String updateAvatar(UUID user_id, MultipartFile file) throws IOException {
        ProfileUserEntity profileUser = profileUserRepository.findByUserId(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Получаем старый URL аватара
        String oldAvatarUrl = profileUser.getAvatarUrl();

        // Если старый аватар существует, удаляем его
        if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty()) {
            String oldFileName = oldAvatarUrl.substring(oldAvatarUrl.lastIndexOf("/") + 1);
            s3Service.deleteFile(oldFileName);  // Удаляем старый файл
        }

        // Загружаем новый аватар
        String fileName = UUID.randomUUID().toString() + ".jpg";
        String avatarUrl = s3Service.uploadFile(file, fileName);

        // Обновляем URL аватара в базе данных
        profileUser.setAvatarUrl(avatarUrl);
        profileUserRepository.save(profileUser);

        // Возвращаем URL нового аватара
        return avatarUrl;
    }

    public void updateInfo(UpdaeInfoRequest updaeInfoRequest) {
        UUID user_id = securityService.getCurrentUser();
        ProfileUserEntity profileUser = profileUserRepository.findByUserId(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        profileUser.setUsername(updaeInfoRequest.getUsername());
        profileUser.setBio(updaeInfoRequest.getBio());
        profileUserRepository.save(profileUser);
    }
}
