package com.example.backend.dtos.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountVerificationDTO {
    @NotBlank(message = "OTP of 6-digits is required.")
    private String otp;
}
