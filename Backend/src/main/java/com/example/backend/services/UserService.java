package com.example.backend.services;

import com.example.backend.dtos.UserRequestDTO;
import com.example.backend.dtos.UserResponseDTO;

public interface UserService {
    UserResponseDTO registration(UserRequestDTO userRequestDTO);
    UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO);
    void deleteUser(Long id);

    // Account Verification
    void sendVerificationEmail(String email);
    void verifyAccount(String email, String otp);
}
