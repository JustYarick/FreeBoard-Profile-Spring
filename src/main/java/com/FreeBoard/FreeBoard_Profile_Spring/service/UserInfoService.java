package com.FreeBoard.FreeBoard_Profile_Spring.service;

import com.FreeBoard.FreeBoard_Profile_Spring.Entity.ProfileUserEntity;
import com.FreeBoard.FreeBoard_Profile_Spring.model.NewUserEvent;
import com.FreeBoard.FreeBoard_Profile_Spring.repository.ProfileUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final ProfileUserRepository profileUserRepository;
    private final S3Service s3Service;

    // создание нового пользователя
    public void CreateNewProfile(NewUserEvent newUserEvent) {

        var newUserProfile = ProfileUserEntity.builder()
                .userId(newUserEvent.getUser_id())
                .email(newUserEvent.getEmail())
                .username(newUserEvent.getUsername())
                .build();

        profileUserRepository.save(newUserProfile);
    }

    public Optional<ProfileUserEntity> GetProfileUser(UUID user_id) {
        return profileUserRepository.findByUserId(user_id);
    }

    /**
     * Метод для обновления аватара пользователя.
     * @param user_id UUID
     * @param file файл с новым аватаром
     * @return URL нового аватара
     */
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
}
