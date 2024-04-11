package com.example.webstoresoftwarepatternsca.ViewModel;

public class CardNumberValidationStrategy implements ValidationStrategy {
    @Override
    public boolean validate(String input) {
        return input != null && input.matches("\\d{16}");
    }
}
