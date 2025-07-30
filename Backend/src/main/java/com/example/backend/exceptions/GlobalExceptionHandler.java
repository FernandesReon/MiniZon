package com.example.backend.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleException(EmailAlreadyExistsException exception) {
        logger.error(exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Email already exists");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestrictionException.class)
    public ResponseEntity<Map<String, String>> handleException(RestrictionException exception) {
        logger.error(exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Operation not allowed");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleException(UserNotFoundException exception) {
        logger.error(exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "User not found");
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    /*
        OTP based exception handlers
     */
    @ExceptionHandler(InvalidOTPException.class)
    public ResponseEntity<Map<String, String>> handleException(InvalidOTPException exception) {
        logger.error(exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Invalid OTP");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OTPExpiredException.class)
    public ResponseEntity<Map<String, String>> handleException(OTPExpiredException exception) {
        logger.error(exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "OTP has expired");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /*
        Authentication based exception handlers - login
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleException(BadCredentialsException exception) {
        logger.error(exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Bad credentials");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, String>> handleException(DisabledException exception) {
        logger.error(exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Account is disabled. Contact your administrator.");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Address exception
    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleException(AddressNotFoundException exception) {
        logger.error(exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Address not found");
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    // Product exception
    @ExceptionHandler(ProductAlreadyExistException.class)
    public ResponseEntity<Map<String, String>> handleException(ProductAlreadyExistException exception) {
        logger.error(exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Product already exists");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Resource exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleException(ResourceNotFoundException exception){
        logger.error(exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Resource not found");
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }
}
