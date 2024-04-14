package com.example.webstoresoftwarepatternsca.ViewModel;

public abstract class DiscountDecorator implements DiscountStrategy {
    protected DiscountStrategy decoratedStrategy;

    public DiscountDecorator(DiscountStrategy decoratedStrategy) {
        this.decoratedStrategy = decoratedStrategy;
    }

    @Override
    public double applyDiscount(double purchaseAmount) {
        return decoratedStrategy.applyDiscount(purchaseAmount);
    }
}
