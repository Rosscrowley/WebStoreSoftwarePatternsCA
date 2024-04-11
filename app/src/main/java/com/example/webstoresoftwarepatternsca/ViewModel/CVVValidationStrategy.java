package com.example.webstoresoftwarepatternsca.ViewModel;

public class CVVValidationStrategy implements ValidationStrategy {
    @Override
    public boolean validate(String input) {
        return input != null && input.matches("\\d{3}");
    }
}
