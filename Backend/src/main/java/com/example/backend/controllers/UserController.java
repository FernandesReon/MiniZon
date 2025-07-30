package com.example.backend.controllers;

import com.example.backend.dtos.product.PageProductResponse;
import com.example.backend.dtos.product.ProductResponseDTO;
import com.example.backend.dtos.user.UserProfileDTO;
import com.example.backend.dtos.user.UserRequestDTO;
import com.example.backend.dtos.user.UserResponseDTO;
import com.example.backend.services.ProductService;
import com.example.backend.services.UserService;
import com.example.backend.views.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final ProductService productService;

    public UserController(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    @PutMapping("/update/id/{id}")
    @PreAuthorize("@userServiceImpl.isUserAuthorized(#id)")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userRequestDTO) {
        try {
            log.info("Controller:: Updating user {}", userRequestDTO);
            UserResponseDTO update = userService.updateUser(id, userRequestDTO);
            log.info("Controller:: Updated user {}", update);
            return ResponseEntity.ok().body(update);
        } catch (Exception e) {
            log.error("Controller :: Unable to update user {}", userRequestDTO, e);
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/remove/id/{id}")
    @PreAuthorize("@userServiceImpl.isUserAuthorized(#id)")
    public ResponseEntity<UserResponseDTO> removeUser(@PathVariable Long id) {
        try {
            log.info("Controller:: Removing user {}", id);
            userService.deleteUser(id);
            return ResponseEntity.ok().body(new UserResponseDTO());
        } catch (Exception e) {
            log.error("Controller :: Unable to remove user {}", id, e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> userProfile(){
        try {
            log.info("Controller:: Fetching user profile");
            UserProfileDTO userProfileDTO = userService.userProfile();
            log.info("Controller:: Fetched user profile");
            return ResponseEntity.ok().body(userProfileDTO);
        } catch (Exception e) {
            log.error("Controller:: Unable to retrieve user profile", e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/products")
    @JsonView(Views.Public.class)
    public ResponseEntity<PageProductResponse> fetchProducts(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size){
        try {
            log.info("Controller:: User fetching all products from pageNo={} pageSize={}", page, size);
            Page<ProductResponseDTO> productPage = productService.getAllProducts(page, size);

            PageProductResponse pageProductResponse = new PageProductResponse(
                    productPage.getContent(),
                    productPage.getNumber() + 1,
                    productPage.getSize(),
                    productPage.getTotalElements(),
                    productPage.getTotalPages(),
                    productPage.isLast()
            );
            return ResponseEntity.ok().body(pageProductResponse);
        } catch (Exception e) {
            log.error("Controller:: Unable to fetch products", e);
            throw new RuntimeException(e);
        }
    }
}
