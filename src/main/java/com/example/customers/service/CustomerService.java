package com.example.customers.service;

import com.example.customers.constants.CustomerConstants;
import com.example.customers.exception.NoSuchCustomerException;
import com.example.customers.model.Customer;
import com.example.customers.repository.CustomerRepository;
import org.springframework.stereotype.Service;


@Service
public class CustomerService {

    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Long checkBalance(String email) throws NoSuchCustomerException {
        final Customer customer = this.customerRepository.findByEmail(email).orElseThrow(() ->
            new NoSuchCustomerException(CustomerConstants.NO_CUSTOMER_BY_EMAIL)
        );
        return customer.getBalance();
    }

    public Long addCredits(String email, long addedBalance) {
        final Customer customer = this.customerRepository.findByEmail(email).orElseThrow();
        customer.setBalance(customer.getBalance() + addedBalance);
        return this.customerRepository.save(customer).getBalance();
    }
}
