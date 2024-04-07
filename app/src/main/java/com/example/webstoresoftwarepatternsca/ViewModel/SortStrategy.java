package com.example.webstoresoftwarepatternsca.ViewModel;

import com.example.webstoresoftwarepatternsca.Model.Product;

import java.util.List;

public interface SortStrategy {
    void sort(List<Product> products);
}