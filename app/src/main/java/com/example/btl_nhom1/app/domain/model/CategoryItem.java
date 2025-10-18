package com.example.btl_nhom1.app.domain.model;

import androidx.annotation.NonNull;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryItem {
    private String name;
    private List<String> subCategories;

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
