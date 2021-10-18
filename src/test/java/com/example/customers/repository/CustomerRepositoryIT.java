package com.example.customers.repository;

import com.example.customers.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerRepositoryIT {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CustomerRepository repository;

    @Test
    void customerRepositoryIsABean() {
        assertTrue(applicationContext.containsBean("customerRepository"));
    }

    @Test
    void testFindByEmail() {
        String firstName = "Aaron";
        String lastName = "Burk";
        String email = "aburk@example.com";
        Long balance = 500L;
        Customer newCustomer = new Customer();
        newCustomer.setFirstName(firstName);
        newCustomer.setLastName(lastName);
        newCustomer.setEmail(email);
        newCustomer.setBalance(balance);
        repository.save(newCustomer);
        Optional<Customer> customer = repository.findByEmail(email);
        assertAll(() -> {
            assertTrue(customer.isPresent());
            assertEquals(firstName, customer.get().getFirstName());
            assertEquals(lastName, customer.get().getLastName());
            assertEquals(balance, customer.get().getBalance());
        });
    }

}