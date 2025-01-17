package com.FreeBoard.FreeBoard_Profile_Spring.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users_info")
public class ProfileUserEntity {
    @Id
    private long id;
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
