package com.example.webstoresoftwarepatternsca.ViewModel;

public class PostalCodeValidationStrategy implements ValidationStrategy {
    @Override
    public boolean validate(String input) {
        return input != null && input.matches("[A-Z]\\d{2}\\s?[A-Z]\\d{3}");
    }
}
