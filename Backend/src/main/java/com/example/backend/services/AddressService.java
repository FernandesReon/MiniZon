package com.example.backend.services;

import com.example.backend.dtos.address.AddressRequestDTO;
import com.example.backend.dtos.address.AddressResponseDTO;

import java.util.List;

public interface AddressService {
    AddressResponseDTO createAddress(AddressRequestDTO addressRequestDTO);
    List<AddressResponseDTO> getAllAddresses();
    AddressResponseDTO updateAddress(Long id, AddressRequestDTO addressRequestDTO);
    void deleteAddress(Long id);
}
