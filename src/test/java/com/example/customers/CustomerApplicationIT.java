package com.example.customers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerApplicationIT {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() { assertTrue(applicationContext.getBeanDefinitionCount() > 0 ); }
}