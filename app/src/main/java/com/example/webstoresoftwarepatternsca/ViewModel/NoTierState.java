package com.example.webstoresoftwarepatternsca.ViewModel;

import com.example.webstoresoftwarepatternsca.Model.User;

public class NoTierState implements LoyaltyState {
    public void checkStatus(User user) {
        if (user.getTotalSpent() >= 100) {
            user.setLoyaltyState(new SilverState());
        }
    }

    public double applyDiscount(double amount) {
        return amount;  // No discount for no tier
    }
}
