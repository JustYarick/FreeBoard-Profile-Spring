package com.FreeBoard.FreeBoard_Profile_Spring;

import com.FreeBoard.FreeBoard_Profile_Spring.exception.UserNotFoundException;
import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.NewUserEventDTO;
import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.ProfileCreatedEventDTO;
import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.UpdaeInfoRequest;
import com.FreeBoard.FreeBoard_Profile_Spring.model.Entity.ProfileUserEntity;
import com.FreeBoard.FreeBoard_Profile_Spring.repository.ProfileUserRepository;
import com.FreeBoard.FreeBoard_Profile_Spring.service.KafkaService;
import com.FreeBoard.FreeBoard_Profile_Spring.service.S3Service;
import com.FreeBoard.FreeBoard_Profile_Spring.service.SecurityService;
import com.FreeBoard.FreeBoard_Profile_Spring.service.UserInfoService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserInfoServiceTest {
    @Mock
    private ProfileUserRepository profileUserRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private KafkaService kafkaService;

    @InjectMocks
    private UserInfoService userInfoService;

    @Test
    void createNewProfile_success() {
        UUID userId = UUID.randomUUID();
        UUID sagaId = UUID.randomUUID();
        NewUserEventDTO dto = NewUserEventDTO.builder()
                .userId(userId)
                .sagaId(sagaId)
                .username("testuser")
                .email("test@example.com")
                .build();

        when(profileUserRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userInfoService.createNewProfile(dto);

        verify(profileUserRepository).save(any(ProfileUserEntity.class));
        verify(kafkaService).sendProfileCreatedEvent(any(ProfileCreatedEventDTO.class));
    }

    @Test
    void createNewProfile_error() {
        UUID userId = UUID.randomUUID();
        UUID sagaId = UUID.randomUUID();
        NewUserEventDTO dto = NewUserEventDTO.builder()
                .userId(userId)
                .sagaId(sagaId)
                .username("testuser")
                .email("test@example.com")
                .build();

        doThrow(new RuntimeException("DB error")).when(profileUserRepository).save(any());

        userInfoService.createNewProfile(dto);

        verify(kafkaService).sendProfileFailedEvent(argThat(event ->
                event.getUserId().equals(userId) &&
                        event.getSagaId().equals(sagaId) &&
                        event.getError().contains("DB error")
        ));
    }

    @Test
    void getCurrentUserProfile_success() {
        UUID userId = UUID.randomUUID();
        ProfileUserEntity user = new ProfileUserEntity(userId);

        try (MockedStatic<SecurityService> mocked = mockStatic(SecurityService.class)) {
            mocked.when(SecurityService::getCurrentUser).thenReturn(userId);
            when(profileUserRepository.findByUserId(userId)).thenReturn(Optional.of(user));

            ProfileUserEntity result = userInfoService.getCurrentUserProfile();

            assertEquals(user, result);
        }
    }


    @Test
    void updateAvatar_addNewAvatar() throws IOException {
        UUID userId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);
        ProfileUserEntity user = new ProfileUserEntity(userId);

        try (MockedStatic<SecurityService> mocked = mockStatic(SecurityService.class)) {
            mocked.when(SecurityService::getCurrentUser).thenReturn(userId);
            when(profileUserRepository.findByUserId(userId)).thenReturn(Optional.of(user));
            when(s3Service.uploadFile(any(), anyString())).thenReturn("newAvatar.jpg");

            String result = userInfoService.updateAvatar(userId, file);

            assertEquals("newAvatar.jpg", result);
            verify(profileUserRepository).save(user);
        }
    }

    @Test
    void updateAvatar_replaceAvatar() throws IOException {
        UUID userId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);
        ProfileUserEntity user = new ProfileUserEntity(userId);
        user.setAvatarFileName("oldAvatar.jpg");

        try (MockedStatic<SecurityService> mocked = mockStatic(SecurityService.class)) {
            mocked.when(SecurityService::getCurrentUser).thenReturn(userId);
            when(profileUserRepository.findByUserId(userId)).thenReturn(Optional.of(user));
            when(s3Service.updateFile(eq("oldAvatar.jpg"), eq(file))).thenReturn("updatedAvatar.jpg");

            String result = userInfoService.updateAvatar(userId, file);

            assertEquals("updatedAvatar.jpg", result);
            verify(profileUserRepository).save(user);
        }
    }

    @Test
    void updateInfo_shouldUpdateBio() {
        UUID userId = UUID.randomUUID();
        ProfileUserEntity user = new ProfileUserEntity(userId);
        UpdaeInfoRequest request = new UpdaeInfoRequest();
        request.setBio("New bio");

        try (MockedStatic<SecurityService> mocked = mockStatic(SecurityService.class)) {
            mocked.when(SecurityService::getCurrentUser).thenReturn(userId);
            when(profileUserRepository.findByUserId(userId)).thenReturn(Optional.of(user));
            when(profileUserRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            ProfileUserEntity updated = userInfoService.updateInfo(request);

            assertEquals("New bio", updated.getBio());
            verify(profileUserRepository).save(user);
        }
    }

    @Test
    void getCurrentUserProfile_userNotFound_shouldThrowException() {
        UUID userId = UUID.randomUUID();

        try (MockedStatic<SecurityService> mocked = mockStatic(SecurityService.class)) {
            mocked.when(SecurityService::getCurrentUser).thenReturn(userId);
            when(profileUserRepository.findByUserId(userId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userInfoService.getCurrentUserProfile());
        }
    }

    @Test
    void updateAvatar_userNotFound_shouldThrowException() {
        UUID userId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);

        when(profileUserRepository.findByUserId(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userInfoService.updateAvatar(userId, file);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void updateInfo_userNotFound_shouldThrowException() {
        UUID userId = UUID.randomUUID();
        UpdaeInfoRequest request = new UpdaeInfoRequest();
        request.setBio("Some bio");

        try (MockedStatic<SecurityService> mocked = mockStatic(SecurityService.class)) {
            mocked.when(SecurityService::getCurrentUser).thenReturn(userId);
            when(profileUserRepository.findByUserId(userId)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                userInfoService.updateInfo(request);
            });

            assertEquals("User not found", exception.getMessage());
        }
    }
}
