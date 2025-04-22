package com.FreeBoard.FreeBoard_Profile_Spring.model.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileFailedEventDTO {
    @NotNull
    private UUID sagaId;

    @NotNull
    private UUID userId;

    @NotNull
    private String error;

    @Builder.Default
    private Instant timestamp = Instant.now();
}
