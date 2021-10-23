package com.example.customers.service;

import com.example.customers.constants.CustomerConstants;
import com.example.customers.exception.CustomerInsufficientFundsException;
import com.example.customers.exception.InsufficientCreditsAddedException;
import com.example.customers.exception.NoSuchCustomerException;
import com.example.customers.model.Customer;
import com.example.customers.repository.CustomerRepository;
import org.springframework.stereotype.Service;


@Service
public class BalanceCalculator implements CustomerService {

    private CustomerRepository customerRepository;

    public BalanceCalculator(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer addCredits(Customer customer, long addedCredit) throws IllegalArgumentException, InsufficientCreditsAddedException {
        validate(customer);
        if (addedCredit < 1) {
           throw new InsufficientCreditsAddedException(CustomerConstants.INSUFFICIENT_CREDITS);
        } else {
           customer.setBalance(customer.getBalance() + addedCredit);
        }
        return this.customerRepository.save(customer);
    }

    public Customer withdrawCredits(Customer customer, long creditsToWithdraw) throws IllegalArgumentException, CustomerInsufficientFundsException {
        validate(customer);
        final Long initialBalance = customer.getBalance();
        if (creditsToWithdraw > initialBalance) {
           throw new CustomerInsufficientFundsException(CustomerConstants.INSUFFICIENT_FUNDS);
        } else {
           customer.setBalance(initialBalance - creditsToWithdraw);
           return this.customerRepository.save(customer);
        }
    }
}
