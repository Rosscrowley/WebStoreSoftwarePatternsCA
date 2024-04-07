package com.example.webstoresoftwarepatternsca.ViewModel;

import com.example.webstoresoftwarepatternsca.Model.Product;

import java.util.Collections;
import java.util.List;

public class AlphabeticalSortStrategy implements SortStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, (p1, p2) -> p1.getTitle().compareToIgnoreCase(p2.getTitle()));
    }
}