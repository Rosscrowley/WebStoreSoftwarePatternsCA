package com.example.webstoresoftwarepatternsca.Model;

public class Product {
    private String productId;
    private String title;
    private double price;
    private String imageUrl;

    public Product() {

    }

    public Product(String productId, String title, double price, String imageUrl) {
        this.productId = productId;
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
