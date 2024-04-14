package com.example.webstoresoftwarepatternsca.Model;

public class Comment {
    private String id;
    private String userId;
    private String text;
    private long timestamp;
    private String productId;

    private float rating;

    public Comment() {

    }

    public Comment(String id, String userId, String text, long timestamp, String productId, float rating) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.timestamp = timestamp;
        this.productId = productId;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}