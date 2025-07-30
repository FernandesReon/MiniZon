package com.example.backend.dtos.product;

import com.example.backend.views.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageProductResponse {
    @JsonView(Views.Public.class)
    private List<ProductResponseDTO> content;

    @JsonView(Views.Public.class)
    private int pageNumber;

    @JsonView(Views.Public.class)
    private int pageSize;

    @JsonView(Views.Public.class)
    private long totalElements;

    @JsonView(Views.Public.class)
    private int totalPages;

    @JsonView(Views.Public.class)
    private boolean last;
}
