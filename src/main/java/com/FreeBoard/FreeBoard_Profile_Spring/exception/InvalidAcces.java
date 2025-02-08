package com.FreeBoard.FreeBoard_Profile_Spring.exception;

public class InvalidAcces extends RuntimeException {
    public InvalidAcces(String message) {
        super(message);
    }

    public InvalidAcces(String message, Throwable cause) {
        super(message, cause);
    }
}
