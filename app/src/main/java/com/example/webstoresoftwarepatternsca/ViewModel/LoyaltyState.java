package com.example.webstoresoftwarepatternsca.ViewModel;

import com.example.webstoresoftwarepatternsca.Model.User;

public interface LoyaltyState {
    void checkStatus(User user);
    double applyDiscount(double amount);
}
