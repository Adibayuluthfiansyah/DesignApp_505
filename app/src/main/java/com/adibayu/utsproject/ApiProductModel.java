package com.adibayu.utsproject;

import com.google.gson.annotations.SerializedName;

public class ApiProductModel {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    @SerializedName("category")
    private String category;

    @SerializedName("image")
    private String imageUrl;

    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Convert API model to local ProductModel
    public ProductModel toProductModel() {
        return new ProductModel(
                id,
                name,
                description,
                (int)(price * 15000), // Convert USD to IDR (approximate)
                R.drawable.hoodiesci, // Default placeholder
                category
        );
    }
}