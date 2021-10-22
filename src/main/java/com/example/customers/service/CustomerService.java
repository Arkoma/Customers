package com.example.customers.service;

import com.example.customers.model.Customer;
import com.example.customers.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Long checkBalance(String email) {
        final Customer customer = this.customerRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        return customer.getBalance();
    }
}
