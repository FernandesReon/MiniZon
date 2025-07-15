package com.example.backend.controllers;

import com.example.backend.dtos.UserRequestDTO;
import com.example.backend.dtos.UserResponseDTO;
import com.example.backend.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        log.info("Controller:: Creating user {}", userRequestDTO);
        UserResponseDTO register = userService.registration(userRequestDTO);
        log.info("Controller:: Saving user {}", register);
        return ResponseEntity.ok().body(register);
    }

    // TODO:: Include Update, Delete, Profile, Login and Logout endpoints
}
