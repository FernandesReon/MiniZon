package com.example.backend.controllers;

import com.example.backend.dtos.product.PageProductResponse;
import com.example.backend.dtos.product.ProductRequestDTO;
import com.example.backend.dtos.product.ProductResponseDTO;
import com.example.backend.dtos.user.UserResponseDTO;
import com.example.backend.services.ProductService;
import com.example.backend.services.UserService;
import com.example.backend.validators.OnCreateProduct;
import com.example.backend.validators.OnUpdateProduct;
import com.example.backend.views.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.groups.Default;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/")
public class AdminController {
    private final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final UserService userService;
    private final ProductService productService;

    public AdminController(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> fetchAllUsers(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size){
        try {
            log.info("Controller:: Fetching all users from pageNo={} pageSize={}", page, size);
            Page<UserResponseDTO> allUsers = userService.getAllUsers(page, size);
            return ResponseEntity.ok().body(allUsers);
        } catch (Exception e) {
            log.error("Controller:: Fetching all users failed", e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(Views.Admin.class)
    public ResponseEntity<PageProductResponse> fetchAllProductsForAdmin(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size){
        try {
            log.info("Controller:: Admin fetching all products from pageNo={} pageSize={}", page, size);
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
            log.error("Controller:: Fetching all products failed", e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/viewProduct/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(Views.Admin.class)
    public ResponseEntity<ProductResponseDTO> fetchProductById(@PathVariable Long id){
        log.info("Controller:: Fetching product with id {}", id);
        ProductResponseDTO product = productService.viewProduct(id);
        log.info("Controller:: Product info fetched for id {}", id);
        return ResponseEntity.ok().body(product);
    }


    @PostMapping("/addProduct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> addProduct(@Validated({OnCreateProduct.class, Default.class})
                                                             @RequestBody ProductRequestDTO productDTO) {
        try {
            log.info("Controller:: Adding new product {}", productDTO);
            ProductResponseDTO newProduct = productService.addNewProduct(productDTO);
            log.info("Controller:: New product saved {}", newProduct);
            return ResponseEntity.ok().body(newProduct);
        } catch (Exception e) {
            log.error("Controller:: Unable to add new product {}", productDTO, e);
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/updateProduct/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id,
                                                            @Validated(OnUpdateProduct.class) @RequestBody ProductRequestDTO productDTO) {
        try {
            log.info("Controller:: Updating product {}", productDTO);
            ProductResponseDTO updatedProduct = productService.updateProduct(id, productDTO);
            log.info("Controller:: Updated product {}", updatedProduct);
            return ResponseEntity.ok().body(updatedProduct);
        } catch (Exception e) {
            log.error("Controller:: Unable to update product {}", productDTO, e);
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/deleteProduct/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            log.warn("Controller:: Deleting product {}", id);
            productService.deleteProduct(id);
            log.info("Controller:: Deleted product {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Controller:: Unable to delete product {}", id, e);
            throw new RuntimeException(e);
        }
    }
}
