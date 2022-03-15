package org.example.app.exceptions;

public class MyNoSuchUserException extends Exception {

    private final String message;

    public MyNoSuchUserException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}