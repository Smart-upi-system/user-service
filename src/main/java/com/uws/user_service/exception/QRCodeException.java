package com.uws.user_service.exception;

public class QRCodeException extends RuntimeException {
    public QRCodeException(String message) {
        super(message);
    }
    public QRCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
