package org.example.app.exceptions;

public class MyNullMessageException extends Exception {

    private final String message;

    public MyNullMessageException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}