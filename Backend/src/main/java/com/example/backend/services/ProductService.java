package com.example.backend.services;

import com.example.backend.dtos.product.ProductRequestDTO;
import com.example.backend.dtos.product.ProductResponseDTO;
import org.springframework.data.domain.Page;


public interface ProductService {
    ProductResponseDTO addNewProduct(ProductRequestDTO productDTO);
    Page<ProductResponseDTO> getAllProducts(int pageNo, int pageSize);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO updateDTO);
    void deleteProduct(Long id);
    ProductResponseDTO viewProduct(Long id);
}
