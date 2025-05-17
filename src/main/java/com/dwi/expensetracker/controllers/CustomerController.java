package com.dwi.expensetracker.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dwi.expensetracker.domains.dtos.CustomerDto;
import com.dwi.expensetracker.domains.entities.CustomerEntity;
import com.dwi.expensetracker.mappers.Mapper;
import com.dwi.expensetracker.services.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final Mapper<CustomerEntity, CustomerDto> customerMapper;

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerDto customerDto) {
        CustomerEntity customerEntity = customerMapper.mapFrom(customerDto);
        CustomerEntity savedCustomerEntity = customerService.save(customerEntity);
        CustomerDto savedDto = customerMapper.mapTo(savedCustomerEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable Long id) {
        return customerService.findOne(id)
                .map(customerMapper::mapTo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<CustomerDto> getAllCustomers(Pageable pageable) {
        return customerService.findAll(pageable)
                .map(customerMapper::mapTo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> fullUpdateCustomer(@PathVariable Long id, @RequestBody CustomerDto customerDto) {
        if (!customerService.doesExist(id)) {
            return ResponseEntity.notFound().build();
        }

        customerDto.setId(id);
        CustomerEntity updatedCustomerEntity = customerService.save(customerMapper.mapFrom(customerDto));
        return ResponseEntity.ok(customerMapper.mapTo(updatedCustomerEntity));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CustomerDto> PartialupdateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerDto customerDto) {
        if (!customerService.doesExist(id)) {
            return ResponseEntity.notFound().build();
        }

        CustomerEntity updatedCustomerEntity = customerService.partialUpdate(id, customerMapper.mapFrom(customerDto));
        return ResponseEntity.ok(customerMapper.mapTo(updatedCustomerEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        if (!customerService.doesExist(id)) {
            return ResponseEntity.notFound().build();
        }

        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
