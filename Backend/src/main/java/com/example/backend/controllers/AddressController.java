package com.example.backend.controllers;

import com.example.backend.dtos.address.AddressRequestDTO;
import com.example.backend.dtos.address.AddressResponseDTO;
import com.example.backend.services.AddressService;
import com.example.backend.validators.OnCreate;
import com.example.backend.validators.OnUpdate;
import jakarta.validation.groups.Default;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/address")
public class AddressController {
    private final Logger log = LoggerFactory.getLogger(AddressController.class);
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/fetch")
    public ResponseEntity<List<AddressResponseDTO>> fetchAll() {
        log.info("Controller:: Fetching all user addresses");
        List<AddressResponseDTO> addressList = addressService.getAllAddresses();
        return ResponseEntity.ok().body(addressList);
    }

    @PostMapping("/add")
    public ResponseEntity<AddressResponseDTO> addAddress(@Validated({OnCreate.class, Default.class})
                                                             @RequestBody AddressRequestDTO addressRequestDTO) {
        log.info("Controller:: Adding user address {}", addressRequestDTO);
        AddressResponseDTO address = addressService.createAddress(addressRequestDTO);
        log.info("Controller:: Address created {}", address);
        return ResponseEntity.ok().body(address);
    }

    @PutMapping("/update/id/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Long id,
                                                            @Validated(OnUpdate.class) @RequestBody AddressRequestDTO addressRequestDTO) {
        log.info("Controller:: Updating user address {}", addressRequestDTO);
        AddressResponseDTO updatedAddress = addressService.updateAddress(id, addressRequestDTO);
        log.info("Controller:: Address updated {}", updatedAddress);
        return ResponseEntity.ok().body(updatedAddress);
    }

    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id) {
        log.info("Controller:: Deleting user address {}", id);
        addressService.deleteAddress(id);
        log.info("Controller:: Address deleted {}", id);
        return ResponseEntity.noContent().build();
    }
}
