package com.example.btl_nhom1.app.domain.model;

import androidx.annotation.NonNull;

import java.util.List;

public class CategoryItem {
    private final String name;
    private final List<String> subCategories;

    public CategoryItem(String name, List<String> subCategories) {
        this.name = name;
        this.subCategories = subCategories;
    }

    public String getName() {
        return name;
    }

    public List<String> getSubCategories() {
        return subCategories;
    }

    // Ghi đè toString để ListView hiển thị đúng tên danh mục chính
    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
