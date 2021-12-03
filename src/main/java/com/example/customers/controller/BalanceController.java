package com.example.customers.controller;

import com.example.customers.exception.InsufficientCreditsAddedException;
import com.example.customers.model.Customer;
import com.example.customers.repository.CustomerRepository;
import com.example.customers.service.BalanceCalculator;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/balance")
public class BalanceController {

    private CustomerRepository customerRepository;
    private BalanceCalculator balanceCalculator;

    public BalanceController(CustomerRepository customerRepository, BalanceCalculator balanceCalculator) {
        this.customerRepository = customerRepository;
        this.balanceCalculator = balanceCalculator;
    }

    @PostMapping("/addCredits/{id}/{amount}")
    @ResponseBody
    public Customer addCredits(@PathVariable Long id, @PathVariable Long amount) throws InsufficientCreditsAddedException {
        Customer customer = customerRepository.getById(id);
        return this.balanceCalculator.addCredits(customer, amount);
    }
}
