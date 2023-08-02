package org.example.exceptions;

public class NoBooksToRemoveException extends RuntimeException {

    public NoBooksToRemoveException() {
        super();
    }

    public NoBooksToRemoveException(String message) {
        super(message);
    }
}
