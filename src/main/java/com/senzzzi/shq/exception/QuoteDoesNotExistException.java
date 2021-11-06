package com.senzzzi.shq.exception;

public class QuoteDoesNotExistException extends RuntimeException {

    public QuoteDoesNotExistException(String message) {
        super(message);
    }
}
