package com.FreeBoard.FreeBoard_Profile_Spring.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users_info")
public class ProfileUserEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID userId;
    private String username;
    private String email;
    @Builder.Default
    private String avatarUrl = "http://localhost:9000/avatars/standart_avatars_giraffe.jpg";
    @Builder.Default
    private String bio = "";
    @Builder.Default
    private Boolean isActive = true;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
}
