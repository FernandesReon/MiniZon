package com.example.backend.mapper;

import com.example.backend.dtos.address.AddressRequestDTO;
import com.example.backend.dtos.address.AddressResponseDTO;
import com.example.backend.models.Address;

public class AddressMapper {
    public static Address addressToEntity(AddressRequestDTO dto) {
        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setZip(dto.getZip());
        address.setCountry(dto.getCountry());
        address.setPhone(dto.getPhone());
        if (dto.getIsDefault() != null) {
            address.setDefault(dto.getIsDefault());
        }
        return address;
    }

    public static AddressResponseDTO entityToDto(Address address) {
        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setZip(address.getZip());
        dto.setCountry(address.getCountry());
        dto.setPhone(address.getPhone());
        dto.setDefault(address.isDefault());
        dto.setCreatedAt(address.getCreatedAt());
        dto.setUpdatedAt(address.getUpdatedAt());
        dto.setUserId(address.getUser().getId());
        return dto;
    }

    public static void updateAddress(Address address, AddressRequestDTO dto) {
        if (dto.getStreet() != null && !dto.getStreet().isBlank()) {
            address.setStreet(dto.getStreet());
        }
        if (dto.getCity() != null && !dto.getCity().isBlank()) {
            address.setCity(dto.getCity());
        }
        if (dto.getState() != null && !dto.getState().isBlank()) {
            address.setState(dto.getState());
        }
        if (dto.getZip() != null && !dto.getZip().isBlank()) {
            address.setZip(dto.getZip());
        }
        if (dto.getCountry() != null && !dto.getCountry().isBlank()) {
            address.setCountry(dto.getCountry());
        }
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            address.setPhone(dto.getPhone());
        }
        if (dto.getIsDefault() != null) {
            address.setDefault(dto.getIsDefault());
        }
    }
}