package com.FreeBoard.FreeBoard_Profile_Spring.controller;

import com.FreeBoard.FreeBoard_Profile_Spring.model.NewUserEvent;
import com.FreeBoard.FreeBoard_Profile_Spring.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaListeners {

    private final UserInfoService userInfoService;

    @KafkaListener(topics = "NewUser", groupId = "Users")
    void newUserRegistered(NewUserEvent newUserData) {
        userInfoService.CreateNewProfile(newUserData);
    }
}
