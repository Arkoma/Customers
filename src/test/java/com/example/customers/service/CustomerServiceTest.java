package com.example.customers.service;

import com.example.customers.constants.CustomerConstants;
import com.example.customers.exception.CustomerInsufficientFundsException;
import com.example.customers.exception.InsufficientCreditsAddedException;
import com.example.customers.exception.NoSuchCustomerException;
import com.example.customers.model.Customer;
import com.example.customers.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.criteria.CriteriaBuilder;
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

    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    final String email = "email@email.com";

    @Test
    void checkBalanceReturnsCustomerBalanceInitializedToZero() throws NoSuchCustomerException {
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
    void checkBalanceReturnsCustomerBalanceWhenNotZero() throws NoSuchCustomerException {
        final long balance = 55L;
        Customer customer = new Customer("First", "Last", email);
        customer.setBalance(balance);
        when(mockCustomerRepository.findByEmail(eq(email))).thenReturn(Optional.of(customer));

        Long checkedBalance = underTest.checkBalance(email);

        assertEquals(balance , checkedBalance);
    }

    @Test
    void checkBalanceThrowsNoSuchCustomerExceptionWhenNoCustomerFromRepositoryFoundByEmail() {
        when(mockCustomerRepository.findByEmail(eq(email))).thenReturn(Optional.empty());
        final NoSuchCustomerException thrownNoSuchCustomerException = assertThrows(
                NoSuchCustomerException.class,
                () -> underTest.checkBalance(email));
        assertEquals(CustomerConstants.NO_CUSTOMER_BY_EMAIL, thrownNoSuchCustomerException.getMessage());
    }

    @Test
    void addCreditsGetsTotalByUpdatingCustomerBalance() throws InsufficientCreditsAddedException, NoSuchCustomerException {
        Customer mockCustomer = mock(Customer.class);
        final long initialBalance = 55L;
        final long addedBalance = 55L;
        mockCustomer.setBalance(55L);
        when(mockCustomerRepository.findByEmail(eq(email))).thenReturn(Optional.of(mockCustomer));
        when(mockCustomerRepository.save(eq(mockCustomer))).thenReturn(mockCustomer);
        when(mockCustomer.getBalance()).thenReturn(initialBalance, initialBalance + addedBalance);

        Long updatedBalance = underTest.addCredits(email, addedBalance);

        assertAll(() -> {
            verify(mockCustomerRepository).findByEmail(stringArgumentCaptor.capture());
            verify(mockCustomerRepository).save(eq(mockCustomer));
            verify(mockCustomer, times(2)).getBalance();
            verify(mockCustomer, times(2/*includes setter inside test*/))
                    .setBalance(longArgumentCaptor.capture());
            assertEquals(email, stringArgumentCaptor.getValue());
            assertEquals(initialBalance + addedBalance, longArgumentCaptor.getValue());
            assertEquals(110L, updatedBalance);
        });
    }

    @Test
    void addCreditsThrowsNoSuchCustomerExceptionWhenNoCustomerFromRepositoryFoundByEmail() {
        when(mockCustomerRepository.findByEmail(eq(email))).thenReturn(Optional.empty());
        final NoSuchCustomerException thrownNoSuchCustomerException = assertThrows(
                NoSuchCustomerException.class,
                () -> underTest.addCredits(email, 1L));
        assertEquals(CustomerConstants.NO_CUSTOMER_BY_EMAIL, thrownNoSuchCustomerException.getMessage());
    }

    @Test
    void addCreditsThrowsInsufficientCreditsAddedExceptionWhenNoOfCreditsBelowOne() {
        when(mockCustomerRepository.findByEmail(email)).thenReturn(Optional.of(new Customer()));
        final InsufficientCreditsAddedException thrownInsufficientCreditsAddedException = assertThrows(
                InsufficientCreditsAddedException.class,
                () -> underTest.addCredits(email, 0L)
        );
        assertEquals(CustomerConstants.INSUFFICIENT_CREDITS, thrownInsufficientCreditsAddedException.getMessage());
    }

    @Test
    void withDrawCreditsThrowsCustomerInsufficientFundsExceptionWhenCreditsRequestedIsGreaterThanBalance() {
        Customer customer = new Customer("First", "Last", email);
        customer.setBalance(10L);
        when(mockCustomerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        final CustomerInsufficientFundsException thrownCustomerInsufficientFundsException = assertThrows(
                CustomerInsufficientFundsException.class,
                () -> underTest.withdrawCredits(email, 50L)
        );
        assertEquals(CustomerConstants.INSUFFICIENT_FUNDS, thrownCustomerInsufficientFundsException.getMessage());
    }
}