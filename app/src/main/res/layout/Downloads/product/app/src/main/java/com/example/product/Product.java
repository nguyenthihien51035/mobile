package com.example.product;

import java.util.List;

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

    public Product(int id, String name, double price, int quantity, String dateOfEntry, String description, String status, String createAt, String updateAt, int categoryId, boolean isDeleted, String primaryImageUrl, List<ProductImage> images, String sku, String goldType, String categoryName, int soldQuantity, String displayName) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.dateOfEntry = dateOfEntry;
        this.description = description;
        this.status = status;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.categoryId = categoryId;
        this.isDeleted = isDeleted;
        this.primaryImageUrl = primaryImageUrl;
        this.images = images;
        this.sku = sku;
        this.goldType = goldType;
        this.categoryName = categoryName;
        this.soldQuantity = soldQuantity;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDateOfEntry() {
        return dateOfEntry;
    }

    public void setDateOfEntry(String dateOfEntry) {
        this.dateOfEntry = dateOfEntry;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getGoldType() {
        return goldType;
    }

    public void setGoldType(String goldType) {
        this.goldType = goldType;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
