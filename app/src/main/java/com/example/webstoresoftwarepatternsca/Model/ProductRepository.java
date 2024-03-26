package com.example.webstoresoftwarepatternsca.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProductRepository {
    private DatabaseReference databaseReference;

    public ProductRepository() {
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
    }

    public void addProduct(Product product) {
        // Use the productId as the key for the product data
        databaseReference.child(product.getProductId()).setValue(product);
    }

    // Methods for updating, deleting, and fetching products can be added here
}