package com.FreeBoard.FreeBoard_Profile_Spring.exception;

import java.time.LocalDateTime;

public record ApiError(
        String path,
        String message,
        int statusCode,
        LocalDateTime localDateTime
) {

}
