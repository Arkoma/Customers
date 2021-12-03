package com.example.customers.controller;

import com.example.customers.model.Customer;
import com.example.customers.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WebAppConfiguration
class CustomerControllerIT {

    private final WebApplicationContext wac;
    private final CustomerRepository customerRepository;
    private final CustomerController customerController;

    @Autowired
    public CustomerControllerIT(WebApplicationContext wac, CustomerRepository customerRepository, CustomerController customerController) {
        this.wac = wac;
        this.customerRepository = customerRepository;
        this.customerController = customerController;
    }

    private MockMvc mockMvc;
    private String requestBody =
            "{\n" +
            "    \"firstName\": \"Aaron\",\n" +
            "    \"lastName\": \"Burk\",\n" +
            "    \"email\": \"aaronburk@example.com\"\n" +
            "}";
    private RequestBuilder requestBuilder;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.customerRepository.deleteAll();
        this.requestBuilder = MockMvcRequestBuilders.post("/customer/new")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.requestBody);
    }

    @Test
    void customerControllerIsABean() {
        assertTrue(this.wac.containsBean("customerController"));
    }

    @Test
    void customerControllerHasCreateUserEndpoint() throws Exception {
        this.mockMvc.perform(this.requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Aaron"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Burk"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("aaronburk@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(0));
    }

    @Test
    void customerControllerHasCustomerRepository() {
        CustomerRepository repositoryOnController = (CustomerRepository) ReflectionTestUtils
                .getField(customerController, "customerRepository");
        assertSame(customerRepository, repositoryOnController);
    }

    @Test
    void customerControllerCreateUserSavesUserToRepository() throws Exception {
        this.mockMvc.perform(this.requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
        Customer customer = customerRepository.findByEmail("aaronburk@example.com").orElseThrow();
        assertAll(() -> {
            assertEquals("Aaron", customer.getFirstName());
            assertEquals("Burk", customer.getLastName());
            assertEquals("aaronburk@example.com", customer.getEmail());
            assertEquals(0L, customer.getBalance());
        });
    }
}