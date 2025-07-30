package com.example.backend.models;

import com.example.backend.views.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long id;

    @JsonView(Views.Public.class)
    private String fileName;

    @JsonView(Views.Public.class)
    private String fileType;

    @JsonView(Views.Public.class)
    private String downloadURL;

    @Lob
    @JsonView(Views.Public.class)
    private Blob image;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "productId")
    @JsonIgnore
    private Product product;
}