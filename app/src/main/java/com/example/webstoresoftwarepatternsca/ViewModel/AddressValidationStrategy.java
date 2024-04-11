package com.example.webstoresoftwarepatternsca.ViewModel;

public class AddressValidationStrategy implements ValidationStrategy {
    @Override
    public boolean validate(String input) {
        return input != null && !input.trim().isEmpty();
    }
}
