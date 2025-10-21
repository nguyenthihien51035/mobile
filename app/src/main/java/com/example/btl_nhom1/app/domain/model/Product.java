package com.example.btl_nhom1.app.domain.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    // alternate có thể map nhiều tên khác nhau từ JSON
    @SerializedName(value = "id", alternate = {"productId"})
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
    private List<ProductSizes> sizes;
    private String sku;
    private String goldType;
    private String categoryName;
    private int soldQuantity;
    private int totalQuantitySold;
    private String displayName;

    public String getPrimaryImageUrl() {
        if (primaryImageUrl != null && !primaryImageUrl.isEmpty()) {
            return primaryImageUrl;
        }

        if (images != null && !images.isEmpty()) {
            // Tìm ảnh primary
            for (ProductImage img : images) {
                if (img.isPrimary()) {
                    return img.getImageUrl();
                }
            }
            return images.get(0).getImageUrl();
        }

        return null;
    }

    public int getSoldQuantity() {
        if (totalQuantitySold > 0) {
            return totalQuantitySold;
        }
        return soldQuantity;
    }
}
