package com.example.customers.service;

import com.example.customers.model.Customer;

public interface CustomerService {

    default void validate(Customer customer) {
        if (customer == null) throw new IllegalArgumentException();
    }
}
