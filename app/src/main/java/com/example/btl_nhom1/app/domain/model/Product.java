package com.example.btl_nhom1.app.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private String dateOfEntry;
    private String description;
    private String status;
    private String createAt;
    private String updateAt;
    private int categoryId;
    private boolean isDeleted;
    private String primaryImageUrl;
    private List<ProductImage> images;
    private String sku;
    private String goldType;
    private String categoryName;
    private int soldQuantity;
    private String displayName;
}
