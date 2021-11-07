package com.senzzzi.shq.exception;

public class QuoteWithoutTextException extends RuntimeException {

    public QuoteWithoutTextException(String message) {
        super(message);
    }
}
