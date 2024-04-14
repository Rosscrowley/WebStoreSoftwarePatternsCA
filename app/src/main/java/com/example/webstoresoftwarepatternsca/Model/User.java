package com.example.webstoresoftwarepatternsca.Model;

import com.example.webstoresoftwarepatternsca.ViewModel.DiscountStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.HolidayDiscountDecorator;
import com.example.webstoresoftwarepatternsca.ViewModel.LoyaltyState;
import com.example.webstoresoftwarepatternsca.ViewModel.NoTierState;

public class User {
    private String userId;
    private String email;
    private String name;

    private CardDetail cardDetail;
    private ShippingAddress shippingAddress;

    private double totalSpent = 0;
    private LoyaltyState loyaltyState;

    private DiscountStrategy discountStrategy;
    public User() {
        this.loyaltyState = new NoTierState();

    }

    public User(String userId, String email, String name, CardDetail cardDetail, ShippingAddress shippingAddress) {
        this();
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.cardDetail = cardDetail;
        this.shippingAddress = shippingAddress;
    }

    public User(String userId) {
        this.userId = userId;
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

    public void addSpending(double amount) {
        this.totalSpent += amount;
        this.loyaltyState.checkStatus(this);
    }

    public void setLoyaltyState(LoyaltyState state) {
        this.loyaltyState = state;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    public double applyDiscount(double amount) {
        if (discountStrategy != null) {
            return discountStrategy.applyDiscount(amount);
        }
        return amount;
    }

    public void activateHolidayPromotion() {
        this.discountStrategy = new HolidayDiscountDecorator(this.discountStrategy);
    }
}
