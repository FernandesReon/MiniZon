package com.example.backend.dtos.product;

import com.example.backend.models.Category;
import com.example.backend.models.ProductImage;
import com.example.backend.views.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {
    @JsonView(Views.Public.class)
    private Long productId;

    @JsonView(Views.Public.class)
    private String productName;

    @JsonView(Views.Public.class)
    private String productDescription;

    @JsonView(Views.Public.class)
    private double productPrice;

    @JsonView(Views.Public.class)
    private int stockQuantity;

    @JsonView(Views.Public.class)
    private boolean productAvailable;

    @JsonView(Views.Public.class)
    private Set<Category> productCategories;

    @JsonView(Views.Public.class)
    private List<ProductImage> productImages;

    @JsonView(Views.Public.class)
    private Set<String> productFeatures;

    @JsonView(Views.Public.class)
    private Set<String> specifications;

    // This fields can't be viewed by normal user.
    @JsonView(Views.Admin.class)
    private LocalDateTime createdAt;

    @JsonView(Views.Admin.class)
    private LocalDateTime updatedAt;

}
