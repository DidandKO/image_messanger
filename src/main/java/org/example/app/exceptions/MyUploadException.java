package org.example.app.exceptions;

public class MyUploadException extends Exception {

    private final String message;

    public MyUploadException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}