package org.example.exceptions;

public class InvalidReturnPersonException extends RuntimeException{
    public InvalidReturnPersonException() {
        super();
    }

    public InvalidReturnPersonException(String message) {
        super(message);
    }
}
