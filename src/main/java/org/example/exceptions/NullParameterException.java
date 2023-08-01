package org.example.exceptions;

public class NullParameterException extends RuntimeException{
    public NullParameterException() {
        super();
    }

    public NullParameterException(String message) {
        super(message);
    }
}
