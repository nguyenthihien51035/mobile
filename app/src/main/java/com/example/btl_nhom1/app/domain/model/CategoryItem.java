package com.example.btl_nhom1.app.domain.model;

import androidx.annotation.NonNull;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryItem {
    private String name;
    private List<String> subCategories;

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
