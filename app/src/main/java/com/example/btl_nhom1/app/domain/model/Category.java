package com.example.btl_nhom1.app.domain.model;

import androidx.annotation.NonNull;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private int id;
    private String name;
    private String bannerUrl;
    private List<Category> children;

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }
}
