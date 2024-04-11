package com.example.webstoresoftwarepatternsca.Model;

public class User {
    private String userId;
    private String email;
    private String name;

    private CardDetail cardDetail;
    private ShippingAddress shippingAddress;

    public User() {

    }

    public User(String userId, String email, String name, CardDetail cardDetail, ShippingAddress shippingAddress) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.cardDetail = cardDetail;
        this.shippingAddress = shippingAddress;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CardDetail getCardDetail() {
        return cardDetail;
    }

    public void setCardDetail(CardDetail cardDetail) {
        this.cardDetail = cardDetail;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
