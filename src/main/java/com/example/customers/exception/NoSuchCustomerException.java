package com.example.customers.exception;

public class NoSuchCustomerException extends Throwable{

    public NoSuchCustomerException() {}

    public NoSuchCustomerException(String message) {
        super(message);
    }
}
