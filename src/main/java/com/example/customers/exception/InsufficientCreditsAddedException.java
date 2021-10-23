package com.example.customers.exception;

public class InsufficientCreditsAddedException extends Throwable {

    public InsufficientCreditsAddedException() {}

    public InsufficientCreditsAddedException(String message) {
        super(message);
    }
}
