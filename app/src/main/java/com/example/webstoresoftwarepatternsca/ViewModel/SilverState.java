package com.example.webstoresoftwarepatternsca.ViewModel;

import com.example.webstoresoftwarepatternsca.Model.User;

public class SilverState implements LoyaltyState {
    public void checkStatus(User user) {
        if (user.getTotalSpent() >= 250) {
          //  user.setLoyaltyState(new GoldState());
        }
    }

    public double applyDiscount(double amount) {
        return amount * 0.95;  // 5% discount
    }

    @Override
    public DiscountStrategy getDiscountStrategy() {
        return amount -> amount * 0.95;
    }
}