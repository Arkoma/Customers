package com.example.customers.controller;

import com.example.customers.exception.NoSuchCustomerException;
import com.example.customers.model.Customer;
import com.example.customers.repository.CustomerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostMapping("/new")
    @ResponseBody
    public Customer newCustomer(@RequestBody Customer customerRequestBody) {
        String firstName = customerRequestBody.getFirstName();
        String lastName = customerRequestBody.getLastName();
        String email = customerRequestBody.getEmail();
        return this.customerRepository.save(new Customer(firstName, lastName, email));
    }

    @GetMapping("/get")
    @ResponseBody
    public Customer getCustomer(@RequestBody Customer customerRequestBody) throws NoSuchCustomerException {
        final String email = customerRequestBody.getEmail();
        return this.customerRepository.findByEmail(email).orElseThrow(NoSuchCustomerException::new);
    }
}
