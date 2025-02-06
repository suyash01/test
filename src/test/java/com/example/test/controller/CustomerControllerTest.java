package com.example.test.controller;

import com.example.test.exception.CustomerNotFoundException;
import com.example.test.model.CustomerDto;
import com.example.test.model.PaginatedDto;
import com.example.test.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        customerDto = new CustomerDto();
        customerDto.setId(1L);
        customerDto.setFirstName("John");
        customerDto.setLastName("Doe");
        customerDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testGetAllCustomers_Success() throws Exception {
        PaginatedDto<CustomerDto> paginatedDto = new PaginatedDto<>();
        paginatedDto.setData(List.of(customerDto));
        paginatedDto.setCurrentPage(0);
        paginatedDto.setTotalPages(1);
        paginatedDto.setTotalItems(1);

        when(customerService.getAllCustomers(anyInt(), anyInt())).thenReturn(paginatedDto);

        mockMvc.perform(get("/customers")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.data[0].firstName").value("John"));
    }

    @Test
    void testGetCustomerById_Success() throws Exception {
        when(customerService.getCustomerById(anyLong())).thenReturn(customerDto);

        mockMvc.perform(get("/customers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testGetCustomerById_NotFound() throws Exception {
        when(customerService.getCustomerById(anyLong())).thenThrow(new CustomerNotFoundException("Customer not found"));

        mockMvc.perform(get("/customers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("Customer not found"));
    }

    @Test
    void testAddCustomer_Success() throws Exception {
        when(customerService.addCustomer(any(CustomerDto.class))).thenReturn(customerDto);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\", \"dateOfBirth\": \"1990-01-01\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testGenericExceptionHandler() throws Exception {
        when(customerService.getCustomerById(anyLong())).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/customers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("Internal Server Error"));
    }
}