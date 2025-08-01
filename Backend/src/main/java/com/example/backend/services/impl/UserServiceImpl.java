package com.example.backend.services.impl;

import com.example.backend.dtos.user.UserLoginDTO;
import com.example.backend.dtos.user.UserProfileDTO;
import com.example.backend.dtos.user.UserRequestDTO;
import com.example.backend.dtos.user.UserResponseDTO;
import com.example.backend.exceptions.*;
import com.example.backend.jwt.JwtResponse;
import com.example.backend.jwt.JwtUtils;
import com.example.backend.mapper.UserMapper;
import com.example.backend.models.User;
import com.example.backend.models.VerificationToken;
import com.example.backend.repository.UserRepository;
import com.example.backend.services.EmailService;
import com.example.backend.services.ProductService;
import com.example.backend.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class UserServiceImpl implements UserService {
    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final ProductServiceImpl productService;

    public UserServiceImpl(UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder,
                           JwtUtils jwtUtils, AuthenticationManager authenticationManager, ProductServiceImpl productService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.productService = productService;
    }

    @Override
    public UserResponseDTO registration(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            log.error("Email already exists");
            throw new EmailAlreadyExistsException("User already exists");
        }

        log.info("Service:: Creating new user");
        User user = UserMapper.toEntity(userRequestDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("Service:: Saving user {}", user);

        try {
            log.info("Service:: Sending verification email");
            sendVerificationEmail(userRequestDTO.getEmail());
            log.info("Service:: Verification email sent");
        } catch (Exception e) {
            log.error("Service:: Error sending verification email");
            throw new RuntimeException(e);
        }

        return UserMapper.responseToUser(savedUser);
    }

    @Override
    public Page<UserResponseDTO> getAllUsers(int pageNo, int pageSize) {
        String authenticatedEmail = productService.verifyAdminAccess();

        log.info("Service:: Fetching all users with pagination: pageNo={}, pageSize={}", pageNo, pageSize);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserMapper::responseToUser);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        if (userRequestDTO.getEmail() != null && !userRequestDTO.getEmail().isBlank()){
            throw new RestrictionException("Updating email is not allowed");
        }
        try {
            log.info("Service:: Updating user {}", user);

            UserMapper.updateUser(user, userRequestDTO);
            user.preUpdate();
            User updatedUser = userRepository.save(user);

            log.info("Service:: Updated user {}", user);
            return UserMapper.responseToUser(updatedUser);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUser(Long id) {
        log.debug("Service:: Request to delete user: {}", id);
        userRepository.deleteById(id);
    }

    // Generate 6-digits OTP
    public static String generateOTP(){
        SecureRandom random = new SecureRandom();
        int otp = random.nextInt(900000) + 100000;
        return String.valueOf(otp);
    }

    // Account Verification
    @Override
    public void sendVerificationEmail(String email) {
        // Check if user exist
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        VerificationToken verificationToken = new VerificationToken();

        // Assign an OTP
        String otp = generateOTP();
        verificationToken.setToken(otp);

        // Set expiry time
        long otpExpiryTime = System.currentTimeMillis() +  (15 * 60 * 1000);
        verificationToken.setExpiryDate(otpExpiryTime);

        verificationToken.setUser(user);

        // save otp to database
        user.setToken(verificationToken);
        userRepository.save(user);

        try {
            emailService.verificationOTP(email, user.getName(), otp);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void verifyAccount(String email, String otp) {
        // Check if user exist
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        VerificationToken verificationToken = user.getToken();

        if (verificationToken == null || verificationToken.getToken() == null) {
            throw new InvalidOTPException("No OTP found or it was already used.");
        }

        if (!verificationToken.getToken().equals(otp)){
            throw new InvalidOTPException("OTP does not match");
        }

        if (verificationToken.getExpiryDate() < System.currentTimeMillis()){
            throw new OTPExpiredException("OTP has expired");
        }

        user.setEmailVerified(true);
        user.setAccountEnabled(true);

        verificationToken.setToken(null);
        verificationToken.setExpiryDate(0L);

        userRepository.save(user);

        try {
            log.info("Service:: Sending welcome email to user {}", user);
            emailService.sendWelcomeEmail(email, user.getName());
            log.info("Service:: Welcome email sent");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Reset Password -> Login
    @Override
    public void sendResetPasswordOTP(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        VerificationToken verificationToken = new VerificationToken();

        String resetOTP = generateOTP();
        verificationToken.setToken(resetOTP);

        long resetOTPExpiry = System.currentTimeMillis() +  (5 * 60 * 1000);
        verificationToken.setExpiryDate(resetOTPExpiry);

        verificationToken.setUser(user);
        user.setToken(verificationToken);
        userRepository.save(user);

        try {
            log.info("Service:: Sending reset password email to user {}", user);
            emailService.resetPassword(email, user.getName(), resetOTP);
            log.info("Service:: Reset Password email sent.");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Verify the reset opt
    @Override
    public void verifyResetPasswordOTP(String email, String otp) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found with email " + email)
        );
        
        VerificationToken verificationToken = user.getToken();
        
        if (verificationToken == null || verificationToken.getToken() == null) {
            throw new InvalidOTPException("No OTP found or it was already used.");
        }
        
        if (!verificationToken.getToken().equals(otp)){
            throw new InvalidOTPException("OTP does not match");
        }
        
        if (verificationToken.getExpiryDate() < System.currentTimeMillis()){
            throw new OTPExpiredException("OTP has expired");
        }
    }


    // Create new Password
    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found with email " + email)
        );

        VerificationToken verificationToken = user.getToken();
        if (verificationToken == null || verificationToken.getToken() == null) {
            throw new InvalidOTPException("No OTP found or it was already used.");
        }

        if (!verificationToken.getToken().equals(otp)){
            throw new InvalidOTPException("OTP does not match");
        }

        if (verificationToken.getExpiryDate() < System.currentTimeMillis()){
            throw new OTPExpiredException("OTP has expired");
        }

        user.setPassword(newPassword);
        userRepository.save(user);

        try {
            log.info("Service:: Sending password reset success acknowledgement email to user {}", user);
            emailService.passwordReset(email, user.getName());
            log.info("Service:: Password reset acknowledgement email sent.");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isUserAuthorized(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmail = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(authenticatedEmail).orElseThrow(
                () -> new UserNotFoundException("User not found with email " + authenticatedEmail)
        );
        return authenticatedUser.getId().equals(id);
    }

    @Override
    public JwtResponse authenticateUser(UserLoginDTO loginDTO) {
        try {
            log.info("Service:: Authenticating user {}", loginDTO);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwtToken = jwtUtils.generateToken((User) userDetails);

            return new JwtResponse(jwtToken);
        } catch (AuthenticationException e) {
            log.error("Service:: Authentication failed", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserProfileDTO userProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticateEmail = authentication.getName();

        User user = userRepository.findByEmail(authenticateEmail).orElseThrow(
                () -> new UserNotFoundException("User not found with email " + authenticateEmail)
        );

        return UserMapper.toProfile(user);
    }
}
