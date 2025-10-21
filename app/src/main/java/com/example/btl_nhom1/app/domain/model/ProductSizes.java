package com.example.btl_nhom1.app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSizes {
    private int id;
    private String size;
    private int quantity;
    private Product product;
}