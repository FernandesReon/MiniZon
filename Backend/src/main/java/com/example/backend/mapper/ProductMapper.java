package com.example.backend.mapper;

import com.example.backend.dtos.product.ProductRequestDTO;
import com.example.backend.dtos.product.ProductResponseDTO;
import com.example.backend.models.Product;
import com.example.backend.models.ProductImage;

public class ProductMapper {
    public static Product maptoProduct(ProductRequestDTO dto) {
        Product product = new Product();
        product.setProductName(dto.getProductName());
        product.setProductDescription(dto.getProductDescription());
        product.setProductPrice(dto.getProductPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setProductCategories(dto.getProductCategories());
        product.setProductFeatures(dto.getProductFeatures());
        product.setSpecifications(dto.getSpecifications());

        if (dto.getProductImages() != null && !dto.getProductImages().isEmpty()) {
            for (ProductImage image : dto.getProductImages()) {
                image.setProduct(product);
            }
            product.setProductImages(dto.getProductImages());
        }

        return product;
    }

    public static ProductResponseDTO productInfoResponse(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setProductDescription(product.getProductDescription());
        dto.setProductPrice(product.getProductPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setProductAvailable(product.isProductAvailable());
        dto.setProductCategories(product.getProductCategories());
        dto.setProductImages(product.getProductImages());
        dto.setProductFeatures(product.getProductFeatures());
        dto.setSpecifications(product.getSpecifications());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

    public static void updateProduct(Product product, ProductRequestDTO dto) {
        if (dto.getProductName() != null && !dto.getProductName().isBlank()) {
            product.setProductName(dto.getProductName());
        }
        if (dto.getProductDescription() != null && !dto.getProductDescription().isBlank()) {
            product.setProductDescription(dto.getProductDescription());
        }
        if (dto.getProductPrice() >= 0) {
            product.setProductPrice(dto.getProductPrice());
        }
        if (dto.getStockQuantity() >= 0) {
            product.setStockQuantity(dto.getStockQuantity());
        }
        if (dto.getProductCategories() != null && !dto.getProductCategories().isEmpty()) {
            product.setProductCategories(dto.getProductCategories());
        }
        if (dto.getProductImages() != null && !dto.getProductImages().isEmpty()) {
            product.setProductImages(dto.getProductImages());
        }
        if (dto.getProductFeatures() != null) {
            product.setProductFeatures(dto.getProductFeatures());
        }
        if (dto.getSpecifications() != null) {
            product.setSpecifications(dto.getSpecifications());
        }
    }
}
