package org.example.exceptions;

public class BookIsNotFoundException extends RuntimeException{
    public BookIsNotFoundException() {
        super();
    }

    public BookIsNotFoundException(String message) {
        super(message);
    }
}
