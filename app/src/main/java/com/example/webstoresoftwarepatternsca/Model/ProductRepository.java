package com.example.webstoresoftwarepatternsca.Model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductRepository {
    private DatabaseReference databaseReference;

    public ProductRepository() {
        databaseReference = FirebaseDatabase.getInstance("https://softwarepatternsca-f1030-default-rtdb.europe-west1.firebasedatabase.app").getReference("products");
    }

    public void addProduct(Product product) {
        // Use the productId as the key for the product data
        databaseReference.child(product.getProductId()).setValue(product);
    }

  //  private MutableLiveData<Set<String>> liveCategories = new MutableLiveData<>();

    public interface DataStatus {
        void DataIsLoaded(List<Product> products, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
        void DataLoadFailed(DatabaseError databaseError);
        void StockLevelLoaded(int stockLevel);
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

    public LiveData<Set<String>> getLiveCategories() {
        MutableLiveData<Set<String>> liveCategories = new MutableLiveData<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> categories = new HashSet<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Product product = child.getValue(Product.class);
                        if (product != null) {
                            if (product.getCategory() != null && !product.getCategory().isEmpty()) {
                                categories.add(product.getCategory());
                               }
                        }
                    }
                }
                liveCategories.setValue(categories);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return liveCategories;
    }

    public LiveData<Set<String>> getLiveManufacturers() {
        MutableLiveData<Set<String>> liveManufacturers = new MutableLiveData<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> manufacturers = new HashSet<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Product product = child.getValue(Product.class);
                    if (product != null && product.getManufacturer() != null && !product.getManufacturer().isEmpty()) {
                        manufacturers.add(product.getManufacturer());
                    }
                }
                liveManufacturers.setValue(manufacturers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        return liveManufacturers;
    }

    public void getProductStock(String productId, final DataStatus callback) {
        databaseReference.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null && product.getStock() > 0) {
                    callback.StockLevelLoaded(product.getStock());
                } else {
                    callback.StockLevelLoaded(0);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.DataLoadFailed(databaseError);
            }
        });
    }

    public void updateProductStock(String productId, int quantityChange) {
        DatabaseReference productRef = databaseReference.child(productId).child("stock");
        productRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentStock = mutableData.getValue(Integer.class);
                if (currentStock == null) {
                    return Transaction.success(mutableData);
                }
                int newStock = currentStock + quantityChange;
                if (newStock < 0) {
                    return Transaction.abort();
                }
                mutableData.setValue(newStock);
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (committed) {
                    Log.d("ProductRepository", "Stock updated.");
                } else {
                    Log.d("ProductRepository", "Failed to update stock.");
                }
            }
        });
    }

}