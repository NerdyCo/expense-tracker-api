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

import com.dwi.expensetracker.domains.dtos.user.UserRequestDto;
import com.dwi.expensetracker.domains.dtos.user.UserBaseDto;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.mappers.Mapper;
import com.dwi.expensetracker.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final UserService customerService;
    private final Mapper<User, UserBaseDto> customerMapper;
    private final Mapper<User, UserRequestDto> createCustomMapper;

    @PostMapping
    public ResponseEntity<UserRequestDto> createCustomer(@RequestBody UserRequestDto createCustomerDto) {
        User customerEntity = createCustomMapper.mapFrom(createCustomerDto);
        User savedCustomerEntity = customerService.save(customerEntity);
        UserRequestDto savedDto = createCustomMapper.mapTo(savedCustomerEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserBaseDto> getCustomer(@PathVariable Long id) {
        return customerService.findOne(id)
                .map(customerMapper::mapTo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<UserBaseDto> getAllCustomers(Pageable pageable) {
        return customerService.findAll(pageable)
                .map(customerMapper::mapTo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserBaseDto> fullUpdateCustomer(@PathVariable Long id, @RequestBody UserBaseDto customerDto) {
        if (!customerService.doesExist(id)) {
            return ResponseEntity.notFound().build();
        }

        customerDto.setId(id);
        User updatedCustomerEntity = customerService.save(customerMapper.mapFrom(customerDto));
        return ResponseEntity.ok(customerMapper.mapTo(updatedCustomerEntity));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserBaseDto> PartialupdateCustomer(
            @PathVariable Long id,
            @RequestBody UserBaseDto customerDto) {
        if (!customerService.doesExist(id)) {
            return ResponseEntity.notFound().build();
        }

        User updatedCustomerEntity = customerService.partialUpdate(id, customerMapper.mapFrom(customerDto));
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
