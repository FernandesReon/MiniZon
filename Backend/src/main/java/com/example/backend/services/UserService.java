package com.example.backend.services;

import com.example.backend.dtos.user.UserLoginDTO;
import com.example.backend.dtos.user.UserProfileDTO;
import com.example.backend.dtos.user.UserRequestDTO;
import com.example.backend.dtos.user.UserResponseDTO;
import com.example.backend.jwt.JwtResponse;
import org.springframework.data.domain.Page;

public interface UserService {
    UserResponseDTO registration(UserRequestDTO userRequestDTO);
    Page<UserResponseDTO> getAllUsers(int pageNo, int pageSize);
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

    // profile
    UserProfileDTO userProfile();
}
