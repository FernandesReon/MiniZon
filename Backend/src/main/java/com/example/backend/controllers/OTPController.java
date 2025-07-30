package com.example.backend.controllers;

import com.example.backend.dtos.authentication.AccountVerificationDTO;
import com.example.backend.dtos.authentication.ResetPasswordDTO;
import com.example.backend.dtos.authentication.VerifyOTPDTO;
import com.example.backend.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/otp")
public class OTPController {
    private final Logger log = LoggerFactory.getLogger(OTPController.class);
    private final UserService userService;

    public OTPController(UserService userService) {
        this.userService = userService;
    }

    // Account Verification -> Registration
    @PostMapping("/verify-account")
    public ResponseEntity<String> verifyAccount(@RequestParam String email,
                                                @Valid @RequestBody AccountVerificationDTO accountVerificationDTO) {
        try {
            log.info("Controller:: Verifying account {}", accountVerificationDTO);
            userService.verifyAccount(email, accountVerificationDTO.getOtp());
            log.info("Controller:: Verified account {}", accountVerificationDTO);
            return ResponseEntity.ok().body("Account verified");
        } catch (Exception e) {
            log.error("Controller:: Error verifying account {}", accountVerificationDTO, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Resend Account Verification OTP -> Registration
    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendVerificationOTP(@RequestParam String email){
        try {
            log.info("Controller:: Resending otp {}", email);
            userService.sendVerificationEmail(email);
            log.info("Controller:: OTP sent {}", email);
            return ResponseEntity.ok().body("OTP sent");
        } catch (Exception e) {
            log.error("Controller:: Error resending otp {}", email, e);
            throw new RuntimeException(e);
        }
    }

    /*
    All endpoints from this point intended before login process
     */

    // Reset Password -> before login
    @PostMapping("/reset-password")
    public ResponseEntity<String> sendResetPasswordOTP(@RequestParam String email){
        try {
            log.info("Controller:: Sending reset password otp {}", email);
            userService.sendResetPasswordOTP(email);
            log.info("Controller:: Reset Password OTP sent {}", email);
            return ResponseEntity.ok().body("OTP sent");
        } catch (Exception e) {
            log.error("Controller:: Error resending otp {}", email, e);
            throw new RuntimeException(e);
        }
    }

    // Opt confirmation -> step before resetting password for login
    @PostMapping("/verify-reset-otp")
    public ResponseEntity<String> verifyResetOTP(@Valid @RequestBody VerifyOTPDTO verifyOTPDTO){
        try {
            log.info("Controller:: Verifying reset otp {}", verifyOTPDTO);
            userService.verifyResetPasswordOTP(verifyOTPDTO.getEmail(), verifyOTPDTO.getOtp());
            log.info("Controller:: Reset Password Verification success {}", verifyOTPDTO);
            return ResponseEntity.ok().body("OTP verified");
        } catch (Exception e) {
            log.error("Controller:: Error verifying reset otp {}", verifyOTPDTO, e);
            throw new RuntimeException(e);
        }
    }

    // Actual password reset endpoint
    @PostMapping("/new-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO){
        try {
            log.info("Controller:: Resetting password {}", resetPasswordDTO);
            userService.resetPassword(resetPasswordDTO.getEmail(), resetPasswordDTO.getOtp(), resetPasswordDTO.getNewPassword());
            log.info("Controller:: Password reset success {}", resetPasswordDTO);
            return ResponseEntity.ok().body("Password reset success");
        } catch (Exception e) {
            log.error("Controller:: Error resetting password {}", resetPasswordDTO, e);
            throw new RuntimeException(e);
        }
    }

    /*
    Other endpoints soon.
     */
}
