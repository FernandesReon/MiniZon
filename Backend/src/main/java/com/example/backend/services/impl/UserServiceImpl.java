package com.example.backend.services.impl;

import com.example.backend.dtos.UserRequestDTO;
import com.example.backend.dtos.UserResponseDTO;
import com.example.backend.exceptions.EmailAlreadyExistsException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.models.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDTO registration(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            log.error("Email already exists");
            throw new EmailAlreadyExistsException("User already exists");
        }
        // TODO: Add logic for verification (send otp)
        log.info("Service:: Creating new user");
        User user = UserMapper.toEntity(userRequestDTO);
        log.info("Service:: Saving user {}", user);
        return UserMapper.responseToUser(userRepository.save(user));
    }

    // TODO:: Include Update, Delete, Verification, Profile services
}
