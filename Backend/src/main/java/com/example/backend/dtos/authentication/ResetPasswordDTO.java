package com.example.backend.dtos.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDTO {
    @NotBlank(message = "Email Address is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "Otp is required.")
    private String otp;

    @NotBlank(message = "Password is required")
    @Size(min = 10, max = 16, message = "Password must consist of 10 to 16 characters")
    private String newPassword;
}
