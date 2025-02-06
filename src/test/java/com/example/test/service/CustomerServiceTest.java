package com.example.test.service;

import com.example.test.exception.CustomerNotFoundException;
import com.example.test.model.CustomerDto;
import com.example.test.model.PaginatedDto;
import com.example.test.repository.CustomerRepository;
import com.example.test.repository.entity.CustomerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerEntity customerEntity;
    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        customerEntity = new CustomerEntity();
        customerEntity.setId(1L);
        customerEntity.setFirstName("John");
        customerEntity.setLastName("Doe");
        customerEntity.setDateOfBirth(LocalDate.of(1990, 1, 1));

        customerDto = new CustomerDto();
        customerDto.setId(1L);
        customerDto.setFirstName("John");
        customerDto.setLastName("Doe");
        customerDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testAddCustomer_Success() {
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customerEntity);

        assertDoesNotThrow(() -> customerService.addCustomer(customerDto));

        verify(customerRepository, times(1)).save(any(CustomerEntity.class));
    }

    @Test
    void testGetAllCustomers_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CustomerEntity> page = new PageImpl<>(List.of(customerEntity));
        when(customerRepository.findAll(pageable)).thenReturn(page);

        PaginatedDto<CustomerDto> result = customerService.getAllCustomers(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalItems());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getCurrentPage());
        assertFalse(result.getData().isEmpty());
        assertEquals("John", result.getData().get(0).getFirstName());
    }

    @Test
    void testGetCustomerById_Success() throws CustomerNotFoundException {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customerEntity));

        CustomerDto result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    @Test
    void testGetCustomerById_NotFound() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(1L));
    }
}
