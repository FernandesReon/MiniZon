package com.example.backend.services;

import com.example.backend.dtos.UserRequestDTO;
import com.example.backend.dtos.UserResponseDTO;

public interface UserService {
    UserResponseDTO registration(UserRequestDTO userRequestDTO);

}
