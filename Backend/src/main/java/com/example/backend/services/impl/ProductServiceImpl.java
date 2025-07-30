package com.example.backend.services.impl;

import com.example.backend.dtos.product.ProductRequestDTO;
import com.example.backend.dtos.product.ProductResponseDTO;
import com.example.backend.exceptions.ProductAlreadyExistException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.mapper.ProductMapper;
import com.example.backend.models.Product;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductServiceImpl implements ProductService {
    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductServiceImpl(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ProductResponseDTO addNewProduct(ProductRequestDTO productDTO) {
        log.info("Service:: Adding new product {}", productDTO);

        String authenticatedEmail = verifyAdminAccess();

        if (productRepository.existsByProductName(productDTO.getProductName())) {
            log.error("Service:: Product already exists with name {}", productDTO.getProductName());
            throw new ProductAlreadyExistException("Product already exists");
        }

        Product product = ProductMapper.maptoProduct(productDTO);
        Product savedProduct = productRepository.save(product);
        log.info("Service:: New product saved {}", productDTO);

        return ProductMapper.productInfoResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAllProducts(int pageNo, int pageSize) {
        log.info("Service:: Fetching all products with pagination: pageNo={}, pageSize={}", pageNo, pageSize);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(ProductMapper::productInfoResponse);
    }


    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO updateDTO) {
        log.info("Service:: Updating product {}", id);
        String authenticatedEmail = verifyAdminAccess();

        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found with ID: " + id)
        );

        if (productRepository.existsByProductName(updateDTO.getProductName())) {
            log.error("Service:: Product already exists with name {}", updateDTO.getProductName());
            throw new ProductAlreadyExistException("Product already exists");
        }

        ProductMapper.updateProduct(product, updateDTO);
        Product updatedProduct = productRepository.save(product);
        log.info("Service:: Product updated {}", product);

        return ProductMapper.productInfoResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        log.warn("Service:: Deleting product {}", id);
        String authenticatedEmail = verifyAdminAccess();
        if (!productRepository.existsById(id)) {
            log.error("Service:: Product not found with id {}", id);
            throw new ResourceNotFoundException("Product with ID " + id + " not found");
        }
        productRepository.deleteById(id);
        log.info("Service:: Product deleted {}", id);
    }

    @Override
    public ProductResponseDTO viewProduct(Long id) {
        log.info("Service:: Viewing product {}", id);
        String authenticatedEmail = verifyAdminAccess();

        Product viewProduct = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found with id " + id)
        );
        return ProductMapper.productInfoResponse(viewProduct);
    }

    public String verifyAdminAccess(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()){
            log.warn("Service:: No authenticated user found");
            throw new AccessDeniedException("User must be authenticated to perform this action");
        }

        String authenticatedEmail = authentication.getName();

        userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("User not found with email: " + authenticatedEmail);
                });

        if (!isAdmin(authentication)){
            log.warn("Service:: User {} attempted to perform and admin action but is not an admin", authenticatedEmail);
            throw new AccessDeniedException("Only admins can perform this action");
        }
        return authenticatedEmail;
    }


    public boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
