package com.example.customers.controller;

import com.example.customers.model.Customer;
import com.example.customers.repository.CustomerRepository;
import com.example.customers.service.BalanceCalculator;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@WebAppConfiguration
class BalanceControllerIT {

    private final WebApplicationContext wac;
    private final BalanceController balanceController;
    private final CustomerRepository customerRepository;
    private final BalanceCalculator balanceCalculator;


    @Autowired
    public BalanceControllerIT(WebApplicationContext wac,
                               BalanceController balanceController,
                               CustomerRepository customerRepository,
                               BalanceCalculator balanceCalculator) {
        this.wac = wac;
        this.balanceController = balanceController;
        this.customerRepository = customerRepository;
        this.balanceCalculator = balanceCalculator;
    }

    private MockMvc mockMvc;
    private RequestBuilder addCreditRequestBuilder;

    public static final String FIRST_NAME = "Testy";
    public static final String LAST_NAME = "Testerton";
    public static final String EMAIL = "testy@example.com";

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.customerRepository.deleteAll();
//        this.addCreditRequestBuilder = MockMvcRequestBuilders.post("balance/addCredits")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//        ;


    }

    @Test
    void balanceControllerIsABean() { assertTrue(this.wac.containsBean("balanceController")); }

    @Test
    void balanceControlerCustomerRepositoryIsInjected() {
        CustomerRepository repositoryOnController = (CustomerRepository) ReflectionTestUtils
                .getField(balanceController, "customerRepository");
        assertSame(customerRepository, repositoryOnController);

    }

    @Test
    void balanceControlerBalanceCalculatorIsInjected() {
        BalanceCalculator balanceCalculatorOnController = (BalanceCalculator) ReflectionTestUtils
                .getField(balanceController, "balanceCalculator");
        assertSame(balanceCalculator, balanceCalculatorOnController);

    }


    @Test
    void balanceControllerAddCredits() throws Exception {
        Customer testedCustomer = new Customer(FIRST_NAME,LAST_NAME,EMAIL);
        Customer savedCustomer = customerRepository.save(testedCustomer);
        final String AMOUNT = "50";
        final String ID = String.valueOf(savedCustomer.getId());
        this.mockMvc
                .perform(post("/balance/addCredits/{id}/{amount}", ID, AMOUNT))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(FIRST_NAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(LAST_NAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(EMAIL))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(AMOUNT));
    }
}