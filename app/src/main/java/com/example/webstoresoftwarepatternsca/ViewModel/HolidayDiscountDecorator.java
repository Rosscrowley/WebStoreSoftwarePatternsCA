package com.example.webstoresoftwarepatternsca.ViewModel;

public class HolidayDiscountDecorator extends DiscountDecorator {
    public HolidayDiscountDecorator(DiscountStrategy decoratedStrategy) {
        super(decoratedStrategy);
    }

    @Override
    public double applyDiscount(double purchaseAmount) {
        // Apply an additional 5% holiday discount on top of other discounts
        double baseDiscountedPrice = super.applyDiscount(purchaseAmount);
        return baseDiscountedPrice * 0.95;
    }
}
