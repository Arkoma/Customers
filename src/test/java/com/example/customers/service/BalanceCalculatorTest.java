package com.example.customers.service;

import com.example.customers.constants.CustomerConstants;
import com.example.customers.exception.CustomerInsufficientFundsException;
import com.example.customers.exception.InsufficientCreditsAddedException;
import com.example.customers.model.Customer;
import com.example.customers.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceCalculatorTest {

    @InjectMocks
    private BalanceCalculator underTest;

    @Mock
    private CustomerRepository mockCustomerRepository;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    @Test
    void addCreditsGetsTotalByUpdatingCustomerBalance() throws IllegalArgumentException, InsufficientCreditsAddedException {
        Customer customer = new Customer();
        final long initialBalance = 55L;
        final long addedBalance = 55L;
        customer.setBalance(initialBalance);
        when(mockCustomerRepository.save(eq(customer))).thenReturn(customer);

        Customer updatedCustomer = underTest.addCredits(customer, addedBalance);

        assertAll(() -> {
            verify(mockCustomerRepository).save(eq(customer));
            assertEquals(110L, updatedCustomer.getBalance());
        });
    }

    @Test
    void addCreditsThrowsInsufficientCreditsAddedExceptionWhenNoOfCreditsBelowOne() {
        final InsufficientCreditsAddedException thrownInsufficientCreditsAddedException = assertThrows(
                InsufficientCreditsAddedException.class,
                () -> underTest.addCredits(mock(Customer.class), 0L)
        );
        assertEquals(CustomerConstants.INSUFFICIENT_CREDITS, thrownInsufficientCreditsAddedException.getMessage());
    }

    @Test
    void withDrawCreditsThrowsCustomerInsufficientFundsExceptionWhenCreditsRequestedIsGreaterThanBalance() {
        Customer mockCustomer = mock(Customer.class);
        mockCustomer.setBalance(10L);
        final CustomerInsufficientFundsException thrownCustomerInsufficientFundsException = assertThrows(
                CustomerInsufficientFundsException.class,
                () -> underTest.withdrawCredits(mockCustomer, 50L)
        );
        assertEquals(CustomerConstants.INSUFFICIENT_FUNDS, thrownCustomerInsufficientFundsException.getMessage());
    }

    @Test
    void customerValidationThrowsIllegalArgumentExceptionWhenValidatingNullCustomer() {
        final IllegalArgumentException thrownIllegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> underTest.validate(null));
    }

    @Test
    void addCreditsThrowsIllegalArgumentExceptionWhenValidatingNullCustomer() {
        final IllegalArgumentException thrownIllegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> underTest.addCredits(null, 50L));
    }

    @Test
    void withdrawCreditsThrowsIllegalArgumentExceptionWhenValidatingNullCustomer() {
        final IllegalArgumentException thrownIllegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> underTest.withdrawCredits(null, 50L));
    }

    @Test
    void customersBalanceAdjustsAppropriatelyWhenCreditsWithdrawn() throws CustomerInsufficientFundsException {
        Customer customer = new Customer();
        customer.setBalance(50L);
        when(mockCustomerRepository.save(eq(customer))).thenReturn(customer);
        Customer updatedCustomer = underTest.withdrawCredits(customer, 10L);
        assertAll(() -> {
            verify(mockCustomerRepository).save(any(Customer.class));
            assertEquals(40L, updatedCustomer.getBalance());
        });
    }
}