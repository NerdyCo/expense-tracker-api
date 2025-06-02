package com.dwi.expensetracker.controllers;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dwi.expensetracker.domains.dtos.user.UserRequestDto;
import com.dwi.expensetracker.domains.dtos.user.UserBaseDto;
import com.dwi.expensetracker.domains.dtos.user.UserPatchDto;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.mappers.Mapper;
import com.dwi.expensetracker.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final Mapper<User, UserBaseDto> userBaseMapper;
    private final Mapper<User, UserRequestDto> userRequestMapper;
    private final Mapper<User, UserPatchDto> userPatchMapper;

    @PostMapping
    public ResponseEntity<UserBaseDto> createUser(@Valid @RequestBody UserRequestDto requestDto) {
        User userToCreate = userRequestMapper.toEntity(requestDto);
        User createdUser = userService.create(userToCreate);
        UserBaseDto responseDto = userBaseMapper.toDto(createdUser);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<UserBaseDto>> getAllUsers(Pageable pageable) {
        Page<UserBaseDto> users = userService.getAll(pageable).map(userBaseMapper::toDto);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserBaseDto> getUserById(@PathVariable UUID id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(userBaseMapper.toDto(user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserBaseDto> updateUserPartially(
            @PathVariable UUID id,
            @Valid @RequestBody UserPatchDto userDto) {
        User updateRequest = userPatchMapper.toEntity(userDto);
        User updatedUser = userService.updatePartial(id, updateRequest);

        return ResponseEntity.ok(userBaseMapper.toDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
