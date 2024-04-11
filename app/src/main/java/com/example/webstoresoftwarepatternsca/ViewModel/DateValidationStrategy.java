package com.example.webstoresoftwarepatternsca.ViewModel;

public class DateValidationStrategy implements ValidationStrategy {
    public boolean validate(String input) {
        // Simple date validation for dd/mm/yy format
        if (input == null || !input.matches("\\d{2}/\\d{2}/\\d{2}")) {
            return false;
        }

        // Split the input into day, month, and year
        String[] parts = input.split("/");
        if (parts.length != 3) {
            return false;
        }

        // Extract day, month, and year as integers
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        // Validate day, month, and year ranges
        if (day < 1 || day > 31 || month < 1 || month > 12 || year < 0) {
            return false;
        }
        return true;
    }
}
