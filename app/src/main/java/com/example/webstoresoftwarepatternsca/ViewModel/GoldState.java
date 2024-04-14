package com.example.webstoresoftwarepatternsca.ViewModel;

import com.example.webstoresoftwarepatternsca.Model.User;

public class GoldState implements LoyaltyState {
    public void checkStatus(User user) {
        if (user.getTotalSpent() >= 500) {
            user.setLoyaltyState(new PlatinumState());
        }
    }

    public double applyDiscount(double amount) {
        return amount * 0.90;  // 10% discount
    }
}
