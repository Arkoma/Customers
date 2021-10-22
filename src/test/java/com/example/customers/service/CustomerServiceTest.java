package com.example.customers.service;

import com.example.customers.model.Customer;
import com.example.customers.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService underTest;

    @Mock
    private CustomerRepository mockCustomerRepository;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    final String email = "email@email.com";

    @Test
    void checkBalanceReturnsCustomerBalanceInitializedToZero() {
        Customer mockCustomer = mock(Customer.class);
        when(mockCustomerRepository.findByEmail(eq(email))).thenReturn(Optional.of(mockCustomer));

        Long balance = underTest.checkBalance(email);

        assertAll(() -> {
            verify(mockCustomerRepository).findByEmail(stringArgumentCaptor.capture());
            verify(mockCustomer).getBalance();
            assertEquals(email, stringArgumentCaptor.getValue());
            assertEquals(0L, balance);
        });

    }

    @Test
    void checkBalanceReturnsCustomerBalanceWhenNotZero() {
        final long balance = 55L;
        Customer customer = new Customer("First", "Last", email);
        customer.setBalance(balance);
        when(mockCustomerRepository.findByEmail(eq(email))).thenReturn(Optional.of(customer));

        Long checkedBalance = underTest.checkBalance(email);

        assertEquals(balance , checkedBalance);
    }

    @Test
    void checkBalanceThrowsEntityNotFoundExceptionWhenNoCustomerFromRepositoryFindByEmail() {
        when(mockCustomerRepository.findByEmail(eq(email))).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> underTest.checkBalance(email));
    }
}