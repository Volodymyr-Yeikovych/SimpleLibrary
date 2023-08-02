package org.example.exceptions;

public class PersonAlreadyLeasingBookException extends RuntimeException {
    public PersonAlreadyLeasingBookException() {
        super();
    }

    public PersonAlreadyLeasingBookException(String message) {
        super(message);
    }
}
