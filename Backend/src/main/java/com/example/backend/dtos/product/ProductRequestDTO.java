package com.example.backend.dtos.product;

import com.example.backend.models.Category;
import com.example.backend.models.ProductImage;
import com.example.backend.validators.OnCreateProduct;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {
    @NotBlank(message = "Product name is required.", groups = OnCreateProduct.class)
    private String productName;

    @NotBlank(message = "Product description is required.", groups = OnCreateProduct.class)
    private String productDescription;

    @Digits(integer = 6, fraction = 2, message = "Product price as to be mentioned.", groups = OnCreateProduct.class)
    private double productPrice;

    @Min(value = 1, message = "Minimum one stock is required.", groups = OnCreateProduct.class)
    private int stockQuantity;

    @NotEmpty(message = "Category of product is compulsory.", groups = OnCreateProduct.class)
    private Set<Category> productCategories;

    @NotEmpty(message = "Minimum of one image is mandatory.", groups = OnCreateProduct.class)
    private List<ProductImage> productImages;

    private Set<String> productFeatures;
    private Set<String> specifications;
}
