package com.example.customers.service;

import com.example.customers.constants.CustomerConstants;
import com.example.customers.exception.CustomerInsufficientFundsException;
import com.example.customers.exception.InsufficientCreditsAddedException;
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

    public Long addCredits(String email, long addedCredit) throws NoSuchCustomerException, InsufficientCreditsAddedException {
        final Customer customer = this.customerRepository.findByEmail(email).orElseThrow(() ->
            new NoSuchCustomerException(CustomerConstants.NO_CUSTOMER_BY_EMAIL)
        );
       if (addedCredit < 1) {
           throw new InsufficientCreditsAddedException(CustomerConstants.INSUFFICIENT_CREDITS);
       } else {
           customer.setBalance(customer.getBalance() + addedCredit);
       }
        return this.customerRepository.save(customer).getBalance();
    }

    public Long withdrawCredits(String email, long creditsToWithdraw) throws CustomerInsufficientFundsException {
       final Customer customer = this.customerRepository.findByEmail(email).orElseThrow();
       if (creditsToWithdraw > customer.getBalance()) {
           throw new CustomerInsufficientFundsException(CustomerConstants.INSUFFICIENT_FUNDS);
       } else {
           return 0L;
       }
    }
}
