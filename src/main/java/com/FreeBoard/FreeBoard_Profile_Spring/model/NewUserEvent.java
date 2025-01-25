package com.FreeBoard.FreeBoard_Profile_Spring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserEvent {

    @JsonProperty("id")
    @NotEmpty
    private UUID user_id;
    @JsonProperty("username")
    @NotEmpty
    private String username;
    @JsonProperty("email")
    @NotEmpty
    private String email;
}
