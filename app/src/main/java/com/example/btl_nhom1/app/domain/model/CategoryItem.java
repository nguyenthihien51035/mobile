package com.example.btl_nhom1.app.domain.model;

import androidx.annotation.NonNull;

import java.util.List;

public class CategoryItem {
    private final int id;
    private final String name;
    private final String bannerUrl;
    private final List<CategoryItem> children;

    public CategoryItem(int id, String name, String bannerUrl, List<CategoryItem> children) {
        this.id = id;
        this.name = name;
        this.bannerUrl = bannerUrl;
        this.children = children;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public List<CategoryItem> getChildren() {
        return children;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
