package com.example.backend.services.impl;

import com.example.backend.dtos.address.AddressRequestDTO;
import com.example.backend.dtos.address.AddressResponseDTO;
import com.example.backend.exceptions.AddressNotFoundException;
import com.example.backend.exceptions.RestrictionException;
import com.example.backend.exceptions.UserNotFoundException;
import com.example.backend.mapper.AddressMapper;
import com.example.backend.models.Address;
import com.example.backend.models.User;
import com.example.backend.repository.AddressRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.services.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {
    private final Logger log = LoggerFactory.getLogger(AddressServiceImpl.class);
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final UserServiceImpl userService;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository, UserServiceImpl userService) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public AddressResponseDTO createAddress(AddressRequestDTO addressRequestDTO) {
        log.info("Service:: Saving new address {}", addressRequestDTO);

        // Check if user is authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmail = authentication.getName();
        User user = userRepository.findByEmail(authenticatedEmail).orElseThrow(
                () -> new UserNotFoundException("User not found with email " + authenticatedEmail)
        );

        // Map to Entity and assign it to authenticated user
        Address address = AddressMapper.addressToEntity(addressRequestDTO);
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        log.info("Service:: Saved address {}", address);
        return AddressMapper.entityToDto(savedAddress);
    }

    @Override
    public List<AddressResponseDTO> getAllAddresses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmail = authentication.getName();

        User user = userRepository.findByEmail(authenticatedEmail).orElseThrow(
                () -> new UserNotFoundException("User not found with email " + authenticatedEmail)
        );

        // Fetch addresses of authenticated user.
        List<Address> addresses = user.getAddressList();
        return addresses.stream().map(AddressMapper::entityToDto).collect(Collectors.toList());
    }

    @Override
    public AddressResponseDTO updateAddress(Long id, AddressRequestDTO addressRequestDTO) {
        log.info("Service:: Updating address with id {}", id);

        Address address = addressRepository.findById(id).orElseThrow(
                () -> new AddressNotFoundException("Address not found")
        );

        if (!userService.isUserAuthorized(address.getUser().getId())) {
            throw new RestrictionException("Unauthorized access to update address");
        }

        AddressMapper.updateAddress(address, addressRequestDTO);
        address.preUpdate();
        Address updatedAddress = addressRepository.save(address);
        return AddressMapper.entityToDto(updatedAddress);
    }

    @Override
    public void deleteAddress(Long id) {
        log.warn("Service:: Deleting address with id {}", id);
        Address address = addressRepository.findById(id).orElseThrow(
                () -> new AddressNotFoundException("Address not found")
        );

        if (!userService.isUserAuthorized(address.getUser().getId())) {
            throw new RestrictionException("Unauthorized access to delete address");
        }
        log.info("Service:: Deleted address {}", address);
        addressRepository.delete(address);
    }
}
