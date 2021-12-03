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

    public static final String FIRST_NAME = "Testy";
    public static final String LAST_NAME = "Testerton";
    public static final String EMAIL = "testy@example.com";


    @Autowired
    public CustomerControllerIT(WebApplicationContext wac, CustomerRepository customerRepository, CustomerController customerController) {
        this.wac = wac;
        this.customerRepository = customerRepository;
        this.customerController = customerController;
    }

    private MockMvc mockMvc;
    private String newCustomerRequestBody =
            "{\n" +
            "    \"firstName\": \"Aaron\",\n" +
            "    \"lastName\": \"Burk\",\n" +
            "    \"email\": \"aaronburk@example.com\"\n" +
            "}";
    private String getCustomerRequestBody =
            "{\n" +
            "    \"email\": " + "\"" + EMAIL + "\"\n" +
            "}";
    private RequestBuilder newCustomerRequestBuilder;
    private RequestBuilder getCustomerRequestBuilder;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.customerRepository.deleteAll();
        this.newCustomerRequestBuilder = MockMvcRequestBuilders.post("/customer/new")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.newCustomerRequestBody);
        this.getCustomerRequestBuilder = MockMvcRequestBuilders.get("/customer/get")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.getCustomerRequestBody);
    }

    @Test
    void customerControllerIsABean() {
        assertTrue(this.wac.containsBean("customerController"));
    }

    @Test
    void customerControllerHasCreateUserEndpoint() throws Exception {
        this.mockMvc.perform(this.newCustomerRequestBuilder)
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
        this.mockMvc.perform(this.newCustomerRequestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
        Customer customer = customerRepository.findByEmail("aaronburk@example.com").orElseThrow();
        assertAll(() -> {
            assertEquals("Aaron", customer.getFirstName());
            assertEquals("Burk", customer.getLastName());
            assertEquals("aaronburk@example.com", customer.getEmail());
            assertEquals(0L, customer.getBalance());
        });

    }

    @Test
    void customerControllerGetCustomerReturnsExpectedCustomer() throws Exception {
        Customer testedCustomer = new Customer(FIRST_NAME, LAST_NAME, EMAIL);
        final Long BALANCE = 50L;
        testedCustomer.setBalance(BALANCE);
        customerRepository.save(testedCustomer);

        this.mockMvc.perform(getCustomerRequestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(FIRST_NAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(LAST_NAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(EMAIL))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(BALANCE));
    }
}