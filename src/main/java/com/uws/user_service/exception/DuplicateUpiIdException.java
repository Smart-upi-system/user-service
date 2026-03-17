package com.uws.user_service.exception;

public class DuplicateUpiIdException extends RuntimeException {
    public DuplicateUpiIdException(String message) {
        super(message);
    }
}
