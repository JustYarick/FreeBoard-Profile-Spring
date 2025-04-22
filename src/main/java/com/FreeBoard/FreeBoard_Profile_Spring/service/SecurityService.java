package com.FreeBoard.FreeBoard_Profile_Spring.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityService {

    private SecurityService() {

    }

    public static UUID getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !authentication.getName().equals("anonymousUser")) {
            return UUID.fromString(authentication.getName());
        }
        return null;
    }
}