package com.ecom.inventory.exception;

public class AdminAccessException extends RuntimeException {

    public AdminAccessException(String message) {
        super(message);
    }

    public AdminAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}