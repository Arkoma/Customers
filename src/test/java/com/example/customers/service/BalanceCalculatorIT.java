package com.example.customers.service;

import com.example.customers.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BalanceCalculatorIT {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private BalanceCalculator balanceCalculator;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void customerServiceIsABean() {
        assertTrue(applicationContext.containsBean("balanceCalculator"));
    }

    @Test
    void customerServiceIncludesCustomerRepository() {
        CustomerRepository injectedCustomerRepository = (CustomerRepository) ReflectionTestUtils.getField(balanceCalculator, "customerRepository");
        assertSame(customerRepository, injectedCustomerRepository);
    }

}