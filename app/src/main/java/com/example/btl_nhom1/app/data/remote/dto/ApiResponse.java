package com.example.btl_nhom1.app.data.remote.dto;

import com.example.btl_nhom1.app.domain.model.Product;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private String message;
    private List<Product> data;
    private String errorMessage;
}
