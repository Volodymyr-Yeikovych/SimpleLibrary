package org.example.exceptions;

public class InvalidPersonAgeException extends RuntimeException{
    public InvalidPersonAgeException() {
        super();
    }

    public InvalidPersonAgeException(String message) {
        super(message);
    }

}
