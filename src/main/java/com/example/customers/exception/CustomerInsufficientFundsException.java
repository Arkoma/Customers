package com.example.customers.exception;

public class CustomerInsufficientFundsException extends Throwable {

    public CustomerInsufficientFundsException() {}

    public CustomerInsufficientFundsException(String message) {
        super(message);
    }
}
