package com.example.webstoresoftwarepatternsca.ViewModel;

public class FilterStrategyFactory {
    public static FilterStrategy getStrategy(String filterType) {
        switch (filterType) {
            case "Category":
                return new CategoryFilterStrategy();
            case "Manufacturer":
                return new ManufacturerFilterStrategy();
            default:
                throw new IllegalArgumentException("No such filter type: " + filterType);
        }
    }
}