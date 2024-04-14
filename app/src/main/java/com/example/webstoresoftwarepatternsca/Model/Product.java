package com.example.webstoresoftwarepatternsca.Model;

import java.io.Serializable;

public class Product implements Serializable {
    private String productId;
    private String title;
    private double price;
    private String imageURL;
    private String manufacturer;
    private String category;
    private float averageRating;
    private int stock;
    public Product() {

    }

    public Product(String productId, String title, double price, String imageUrl, float averageRating ) {
        this.productId = productId;
        this.title = title;
        this.price = price;
        this.imageURL = imageUrl;
        this.averageRating = averageRating;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getImageUrl() { return imageURL; }
    public void setImageUrl(String imageUrl) { this.imageURL = imageUrl; }
    public String getManufacturer() {return manufacturer; }

    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

}
