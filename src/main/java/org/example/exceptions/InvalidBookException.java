package org.example.exceptions;

public class InvalidBookException extends RuntimeException{
    public InvalidBookException() {
        super();
    }

    public InvalidBookException(String message) {
        super(message);
    }
}
