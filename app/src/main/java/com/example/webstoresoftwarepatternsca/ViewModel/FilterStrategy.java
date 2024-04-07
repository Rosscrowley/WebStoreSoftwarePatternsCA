package com.example.webstoresoftwarepatternsca.ViewModel;

import com.example.webstoresoftwarepatternsca.Model.Product;

import java.util.List;
import java.util.Set;

public interface FilterStrategy {
    List<Product> filter(List<Product> allProducts, Set<String> criteria);
}
