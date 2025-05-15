package com.dwi.expensetracker.unit.repositories;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.CustomerEntity;
import com.dwi.expensetracker.repositories.CustomerRepository;
import com.dwi.expensetracker.services.impl.CustomerServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CustomerRepositoryUnitTests {

    private static final Long CUSTOMER_ID = 1L;

    private CustomerEntity customer;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    public void setup() {
        customer = new CustomerEntity();
        customer.setId(CUSTOMER_ID);
    }

    @Test
    public void shouldDeleteCustomerById() {
        customerService.delete(CUSTOMER_ID);

        verify(customerRepository).deleteById(CUSTOMER_ID);
    }

    @Test
    public void shouldReturnTrueWhenCustomerExists() {
        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);

        boolean foundCustomer = customerService.doesExist(CUSTOMER_ID);

        assertThat(foundCustomer).isTrue();
    }

    @Test
    public void shouldReturnEmptyWhenCustomerNotFound() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        Optional<CustomerEntity> result = customerService.findOne(CUSTOMER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnPagedCustomers() {
        PageRequest pageable = PageRequest.of(0, 10);
        List<CustomerEntity> customers = List.of(customer);
        PageImpl<CustomerEntity> page = new PageImpl<>(customers);

        when(customerRepository.findAll(pageable)).thenReturn(page);

        Page<CustomerEntity> foundCustomers = customerService.findAll(pageable);

        assertThat(foundCustomers.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void shouldReturnCustomerWhenFound() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

        Optional<CustomerEntity> foundCustomer = customerService.findOne(CUSTOMER_ID);

        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer).containsSame(customer);
    }

    @Test
    public void shouldPartialUpdateCustomer() {
        CustomerEntity existingCustomer = TestDataUtil.createTestCustomerEntityA();
        CustomerEntity incomingCustomer = TestDataUtil.createTestCustomerEntityB();

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(existingCustomer));
        when(passwordEncoder.encode(incomingCustomer.getPassword())).thenReturn("hashedPass");
        when(customerRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        CustomerEntity updatedCustomer = customerService.partialUpdate(CUSTOMER_ID, incomingCustomer);

        assertThat(updatedCustomer.getUsername()).isEqualTo("teguh");
        assertThat(updatedCustomer.getPassword()).isEqualTo("hashedPass");
    }

    @Test
    public void shouldNotUpdateAnythingWhenAllFieldAreNull() {
        CustomerEntity existingCustomer = TestDataUtil.createTestCustomerEntityA();
        CustomerEntity nullIncomingCustomer = new CustomerEntity();

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        CustomerEntity updatedCustomer = customerService.partialUpdate(CUSTOMER_ID, nullIncomingCustomer);

        assertThat(updatedCustomer.getUsername()).isEqualTo("kautsar");
        assertThat(updatedCustomer.getEmail()).isEqualTo("kautsar@gmail.com");
        assertThat(updatedCustomer.getPassword()).isEqualTo("kautsar123");

        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    public void shouldSaveCustomer() {
        customer.setUsername("test");

        when(customerRepository.save(customer)).thenReturn(customer);

        CustomerEntity savedCustomer = customerService.save(customer);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getUsername()).isEqualTo("test");

    }
}
