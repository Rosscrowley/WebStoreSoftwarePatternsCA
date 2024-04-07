package com.example.webstoresoftwarepatternsca.ViewModel;

import com.example.webstoresoftwarepatternsca.Model.Product;

import java.util.Collections;
import java.util.List;

public class PriceLowToHighSortStrategy implements SortStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
    }
}