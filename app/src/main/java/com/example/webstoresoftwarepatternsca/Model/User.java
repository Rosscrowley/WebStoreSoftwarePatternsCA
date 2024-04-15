package com.example.webstoresoftwarepatternsca.Model;

import com.example.webstoresoftwarepatternsca.ViewModel.DiscountStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.GoldState;
import com.example.webstoresoftwarepatternsca.ViewModel.HolidayDiscountDecorator;
import com.example.webstoresoftwarepatternsca.ViewModel.LoyaltyState;
import com.example.webstoresoftwarepatternsca.ViewModel.NoTierState;
import com.example.webstoresoftwarepatternsca.ViewModel.PlatinumState;
import com.example.webstoresoftwarepatternsca.ViewModel.SilverState;

public class User {
    private String userId;
    private String email;
    private String name;

    private CardDetail cardDetail;
    private ShippingAddress shippingAddress;

    private double totalSpent = 0;

    private String loyaltyTier;
    public User() {

    }

    public User(String userId, String email, String name, CardDetail cardDetail, ShippingAddress shippingAddress, String loyaltyTier) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.cardDetail = cardDetail;
        this.shippingAddress = shippingAddress;
        this.loyaltyTier = (loyaltyTier != null ? loyaltyTier : "No Tier");
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
    }


    public double getTotalSpent() {
        return totalSpent;
    }

    public String getLoyaltyTier() {
        return loyaltyTier;
    }

    public void setLoyaltyTier(String loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
    }

    public double applyDiscount(double amount) {
          switch (this.loyaltyTier) {
            case "Platinum":
                return amount * 0.85;
            case "Gold":
                return amount * 0.90;
            case "Silver":
                return amount * 0.95;
            default:
                return amount; // No discount
        }
    }
}
