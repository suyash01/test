package com.example.test.service;

import com.example.test.exception.CustomerNotFoundException;
import com.example.test.model.CustomerDto;
import com.example.test.model.PaginatedDto;
import com.example.test.repository.CustomerRepository;
import com.example.test.repository.entity.CustomerEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerDto addCustomer(CustomerDto customerDto) {
        log.info("Started adding customer...");
        final CustomerEntity customer = mapCustomerEntity(customerDto);
        CustomerEntity customerEntity = customerRepository.save(customer);
        log.info("Successfully added customer...");
        return mapCustomerDto(customerEntity);
    }

    public PaginatedDto<CustomerDto> getAllCustomers(int page, int size) {
        log.info("Fetching all customers with pagination - Page: {}, Size: {}", page, size);
        final Pageable pageable = PageRequest.of(page, size);
        final Page<CustomerEntity> customerPage = customerRepository.findAll(pageable);
        final List<CustomerDto> customerDtos = customerPage.getContent().stream().map(this::mapCustomerDto).collect(Collectors.toList());

        final PaginatedDto<CustomerDto> response = new PaginatedDto<>();
        response.setData(customerDtos);
        response.setCurrentPage(customerPage.getNumber());
        response.setTotalPages(customerPage.getTotalPages());
        response.setTotalItems(customerPage.getTotalElements());
        log.info("Successfully fetched all customers with pagination - Page: {}, Size: {}", page, size);

        return response;
    }

    public CustomerDto getCustomerById(Long id) throws CustomerNotFoundException {
        log.info("Fetching customer by id {}", id);
        final Optional<CustomerEntity> customerEntity = customerRepository.findById(id);
        if(customerEntity.isPresent()) {
            log.info("Successfully fetched customer by id {}", id);
            return mapCustomerDto(customerEntity.get());
        }
        throw new CustomerNotFoundException("Customer not found with id " + id);
    }

    // Could use mapstruct
    private CustomerEntity mapCustomerEntity(CustomerDto customerDto) {
        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName(customerDto.getFirstName());
        customerEntity.setLastName(customerDto.getLastName());
        customerEntity.setDateOfBirth(customerDto.getDateOfBirth());
        return customerEntity;
    }

    // Could use mapstruct
    private CustomerDto mapCustomerDto(CustomerEntity customerEntity) {
        final CustomerDto customerDto = new CustomerDto();
        customerDto.setId(customerEntity.getId());
        customerDto.setFirstName(customerEntity.getFirstName());
        customerDto.setLastName(customerEntity.getLastName());
        customerDto.setDateOfBirth(customerEntity.getDateOfBirth());
        return customerDto;
    }
}
