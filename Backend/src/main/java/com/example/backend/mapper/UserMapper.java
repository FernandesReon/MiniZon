package com.example.backend.mapper;

import com.example.backend.dtos.UserProfileDTO;
import com.example.backend.dtos.UserRequestDTO;
import com.example.backend.dtos.UserResponseDTO;
import com.example.backend.models.User;

public class UserMapper {
    // Incoming data from frontend gets saved in database
    public static User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }

    // Data from database provided to user (all fields)
    public static UserResponseDTO responseToUser(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setAccountEnabled(user.isAccountEnabled());
        dto.setRoles(user.getRoles());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    // Minimal info about user (profile)
    public static UserProfileDTO toProfile(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());
        return dto;
    }

    // Update existing user info
    public static void updateUser(User existingUser, UserRequestDTO dto) {
        if (dto.getName() != null && !dto.getName().isBlank()) {
            existingUser.setName(dto.getName());
        }

        if (dto.getPassword() != null && dto.getPassword().isBlank()) {
            existingUser.setPassword(dto.getPassword());
        }
    }
}
