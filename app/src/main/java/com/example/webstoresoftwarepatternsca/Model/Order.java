package com.example.webstoresoftwarepatternsca.Model;

import java.util.List;

public class Order {
    private String orderId;
    private String userId;
    private List<CartItem> cartItems;
    private double totalAmount;
    private CardDetail cardDetail;
    private ShippingAddress shippingAddress;

    public Order() {

    }

    public Order(String orderId, String userId, List<CartItem> cartItems, double totalAmount, CardDetail cardDetail, ShippingAddress shippingAddress) {
        this.orderId = orderId;
        this.userId = userId;
        this.cartItems = cartItems;
        this.totalAmount = totalAmount;
        this.cardDetail = cardDetail;
        this.shippingAddress = shippingAddress;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

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
