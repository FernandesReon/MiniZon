package com.example.backend.services;

import com.example.backend.dtos.UserLoginDTO;
import com.example.backend.dtos.UserRequestDTO;
import com.example.backend.dtos.UserResponseDTO;
import com.example.backend.jwt.JwtResponse;

public interface UserService {
    UserResponseDTO registration(UserRequestDTO userRequestDTO);
    UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO);
    void deleteUser(Long id);

    // Account Verification
    void sendVerificationEmail(String email);
    void verifyAccount(String email, String otp);

    // Reset Password
    void sendResetPasswordOTP(String email);
    void verifyResetPasswordOTP(String email, String otp);
    void resetPassword(String email, String otp, String newPassword);

    // login
    boolean isUserAuthorized(Long id);
    JwtResponse authenticateUser(UserLoginDTO loginDTO);
}
