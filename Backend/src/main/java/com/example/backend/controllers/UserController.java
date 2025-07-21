package com.example.backend.controllers;

import com.example.backend.dtos.*;
import com.example.backend.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/update/id/{id}")
    @PreAuthorize("@userServiceImpl.isUserAuthorized(#id)")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userRequestDTO) {
        try {
            log.info("Controller:: Updating user {}", userRequestDTO);
            UserResponseDTO update = userService.updateUser(id, userRequestDTO);
            log.info("Controller:: Updated user {}", update);
            return ResponseEntity.ok().body(update);
        } catch (Exception e) {
            log.error("Controller :: Unable to update user {}", userRequestDTO, e);
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/remove/id/{id}")
    @PreAuthorize("@userServiceImpl.isUserAuthorized(#id)")
    public ResponseEntity<UserResponseDTO> removeUser(@PathVariable Long id) {
        try {
            log.info("Controller:: Removing user {}", id);
            userService.deleteUser(id);
            return ResponseEntity.ok().body(new UserResponseDTO());
        } catch (Exception e) {
            log.error("Controller :: Unable to remove user {}", id, e);
            throw new RuntimeException(e);
        }
    }
}
