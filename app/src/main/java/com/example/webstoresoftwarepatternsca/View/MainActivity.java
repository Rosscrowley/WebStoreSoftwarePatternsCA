package com.example.webstoresoftwarepatternsca.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.webstoresoftwarepatternsca.Model.Product;
import com.example.webstoresoftwarepatternsca.Model.ProductRepository;
import com.example.webstoresoftwarepatternsca.R;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private ProductRepository productRepository = new ProductRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, productList);

        productAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {

                openProductDetailFragment(product);
            }
        });

        productsRecyclerView.setAdapter(productAdapter);

        // Fetch products and update UI
        productRepository.getProducts(new ProductRepository.DataStatus() {
            @Override
            public void DataIsLoaded(List<Product> products, List<String> keys) {
                productList.clear();
                productList.addAll(products);
                productAdapter.notifyDataSetChanged(); // Refresh the adapter with the new data
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
    }

    private void openProductDetailFragment(Product product) {
        ProductDetailFragment fragment = ProductDetailFragment.newInstance(product);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
