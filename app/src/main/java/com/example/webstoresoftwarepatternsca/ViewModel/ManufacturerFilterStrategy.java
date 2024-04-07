package com.example.webstoresoftwarepatternsca.ViewModel;

import com.example.webstoresoftwarepatternsca.Model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ManufacturerFilterStrategy implements FilterStrategy {
    @Override
    public List<Product> filter(List<Product> allProducts, Set<String> criteria) {
        if (criteria.isEmpty()) {
            return allProducts;
        }
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : allProducts) {
            if (criteria.contains(product.getManufacturer())) {
                filteredProducts.add(product);
            }
        }
        return filteredProducts;
    }
}
