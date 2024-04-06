package com.example.webstoresoftwarepatternsca.Model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductRepository {
    private DatabaseReference databaseReference;

    public ProductRepository() {
        databaseReference = FirebaseDatabase.getInstance("https://softwarepatternsca-f1030-default-rtdb.europe-west1.firebasedatabase.app").getReference("products");
    }

    public void addProduct(Product product) {
        // Use the productId as the key for the product data
        databaseReference.child(product.getProductId()).setValue(product);
    }

    public interface DataStatus {
        void DataIsLoaded(List<Product> products, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
        void DataLoadFailed(DatabaseError databaseError);
    }

    public void getProducts(final DataStatus dataStatus) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Product> products = new ArrayList<>();
                List<String> productIds = new ArrayList<>(); // To keep track of product IDs
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null) {
                        String productId = productSnapshot.getKey();
                        product.setProductId(productId);
                        products.add(product);
                        productIds.add(productId);
                    }
                }
                dataStatus.DataIsLoaded(products, productIds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dataStatus.DataLoadFailed(databaseError);
            }
        });
    }

    public void getProduct(String productId, final DataStatus callback) {
        databaseReference.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null) {
                    product.setProductId(dataSnapshot.getKey());
                    callback.DataIsLoaded(Collections.singletonList(product), Collections.singletonList(dataSnapshot.getKey()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.DataLoadFailed(databaseError);
            }
        });
    }

}