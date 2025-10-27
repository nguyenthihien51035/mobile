package com.example.btl_nhom1.app.data.remote.dto.res;

import com.example.btl_nhom1.app.domain.model.Product;

import java.util.List;

import lombok.Getter;

@Getter
public class ProductPageResponse {
    private List<Product> content;
    private int pageNumber;
    private int totalPages;
}
