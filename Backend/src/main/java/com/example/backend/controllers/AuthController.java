package com.example.backend.controllers;

import com.example.backend.dtos.user.UserLoginDTO;
import com.example.backend.dtos.user.UserRequestDTO;
import com.example.backend.dtos.user.UserResponseDTO;
import com.example.backend.jwt.JwtResponse;
import com.example.backend.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/auth")
public class AuthController {
    private final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        try {
            log.info("Controller:: Creating user {}", userRequestDTO);
            UserResponseDTO register = userService.registration(userRequestDTO);
            log.info("Controller:: Saving user {}", register);
            return ResponseEntity.ok().body(register);
        } catch (Exception e) {
            log.error("Controller:: Creating user failed", e);
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletResponse response) {
        try {
            log.info("Controller:: Login user {}", userLoginDTO);
            JwtResponse jwtToken = userService.authenticateUser(userLoginDTO);

            try {
                Cookie cookie = new Cookie("JWT", jwtToken.getToken());

                cookie.setHttpOnly(true);
                cookie.setSecure(false);    // for testing purpose false - else true
                cookie.setPath("/");
                cookie.setMaxAge(24 * 60 * 60);
                cookie.setAttribute("SameSite", "Strict");

                response.addCookie(cookie);
                log.info("Controller:: Cookie added successfully");
            } catch (Exception e) {
                log.error("Controller:: Error while adding cookie", e);
                throw new RuntimeException(e);
            }

            log.info("Controller:: Successful login {}", jwtToken);
            return ResponseEntity.ok().body(jwtToken);
        } catch (BadCredentialsException | DisabledException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
