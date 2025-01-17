package com.FreeBoard.FreeBoard_Profile_Spring.service;

import com.FreeBoard.FreeBoard_Profile_Spring.Entity.ProfileUserEntity;
import com.FreeBoard.FreeBoard_Profile_Spring.model.NewUserEvent;
import com.FreeBoard.FreeBoard_Profile_Spring.repository.ProfileUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final ProfileUserRepository profileUserRepository;

    // создание нового пользователя
    public void CreateNewProfile(NewUserEvent newUserEvent) {

        var newUserProfile = ProfileUserEntity.builder()
                .id(newUserEvent.getId())
                .email(newUserEvent.getEmail())
                .username(newUserEvent.getUsername())
                .build();

        profileUserRepository.save(newUserProfile);
    }

    public Optional<ProfileUserEntity> GetProfileUser(String email) {
        return profileUserRepository.findByEmail(email);
    }

    public  void UpdateAvatarUrl(String email, String avatarUrl) {
        ProfileUserEntity profileUser = profileUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Обновляем поле avatarUrl
        profileUser.setAvatarUrl(avatarUrl);

        // Сохраняем изменения в базе данных
        profileUserRepository.save(profileUser);
    }
}
