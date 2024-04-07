package com.example.webstoresoftwarepatternsca.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.webstoresoftwarepatternsca.Model.Product;
import com.example.webstoresoftwarepatternsca.Model.ProductRepository;
import com.example.webstoresoftwarepatternsca.R;
import com.example.webstoresoftwarepatternsca.ViewModel.AlphabeticalSortStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.FeatureSortStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.PriceHighToLowSortStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.PriceLowToHighSortStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.SortStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> originalList = new ArrayList<>();
    private ProductRepository productRepository = new ProductRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, productList);


        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_item_home) {

                return true;
            } else if (itemId == R.id.menu_item_shopping_cart) {
                openCartActivity();
                return true;
            }

            return false;
        });
        productAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {

                openProductDetailFragment(product);
            }
        });

        productsRecyclerView.setAdapter(productAdapter);

        productRepository.getProducts(new ProductRepository.DataStatus() {
            @Override
            public void DataIsLoaded(List<Product> products, List<String> keys) {
                productList.clear();
                productList.addAll(products);
                originalList = new ArrayList<>(productList);
                productAdapter.notifyDataSetChanged();

                productAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
                    @Override
                    public void onProductClick(Product product) {
                        onProductSelected(product.getProductId());
                    }
                });
            }

            @Override
            public void DataIsInserted() {}

            @Override
            public void DataIsUpdated() {}

            @Override
            public void DataIsDeleted() {}

            @Override
            public void DataLoadFailed(DatabaseError databaseError) {
                Log.w("MainActivity", "Failed to read value.", databaseError.toException());
            }
        });


        Spinner sortSpinner = findViewById(R.id.spinner2);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Feature
                        updateSort(new FeatureSortStrategy());
                        break;
                    case 1: // Price high to low
                        updateSort(new PriceHighToLowSortStrategy());
                        break;
                    case 2: // Price low to high
                        updateSort(new PriceLowToHighSortStrategy());
                        break;
                    case 3: // Alphabetically
                        updateSort(new AlphabeticalSortStrategy());
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No sorting applied
            }
        });
    }

    public void onProductSelected(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance("https://softwarepatternsca-f1030-default-rtdb.europe-west1.firebasedatabase.app").getReference("products").child(productId);

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null) {
                    product.setProductId(dataSnapshot.getKey()); // Set the productId based on the key from Firebase
                    openProductDetailFragment(product);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void openProductDetailFragment(Product product) {
        ProductDetailFragment fragment = ProductDetailFragment.newInstance(product);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openCartActivity() {
        Intent intent = new Intent(MainActivity.this, CartActivity.class);
        startActivity(intent);
    }

    public void updateSort(SortStrategy sortStrategy) {
        if (sortStrategy instanceof FeatureSortStrategy) {
            productList.clear();
            productList.addAll(originalList);
            productAdapter.notifyDataSetChanged();
        } else {
            sortStrategy.sort(productList);
            productAdapter.notifyDataSetChanged();
        }
    }
}

