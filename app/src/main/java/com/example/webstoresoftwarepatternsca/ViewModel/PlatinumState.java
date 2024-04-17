package com.example.webstoresoftwarepatternsca.ViewModel;

import com.example.webstoresoftwarepatternsca.Model.User;

public class PlatinumState implements LoyaltyState {
    public void checkStatus(User user) {
        // No further state beyond Platinum
    }
    public double applyDiscount(double amount) {
        return amount * 0.85;  // 15% discount
    }
    @Override
    public DiscountStrategy getDiscountStrategy() {
        return amount -> amount * 0.85;
    }
}
