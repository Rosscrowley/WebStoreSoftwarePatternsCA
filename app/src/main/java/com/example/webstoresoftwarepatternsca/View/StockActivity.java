package com.example.webstoresoftwarepatternsca.View;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webstoresoftwarepatternsca.Model.Product;
import com.example.webstoresoftwarepatternsca.Model.ProductRepository;
import com.example.webstoresoftwarepatternsca.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class StockActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StockAdapter stockAdapter;
    private ProductRepository productRepository;
    private SearchView searchView;

    private BottomNavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.menu_item_home) {

                Intent customersIntent = new Intent(StockActivity.this, MainActivity.class);
                startActivity(customersIntent);
                return true;
            } else if (itemId == R.id.menu_item_customers) {

                Intent customerIntent = new Intent(StockActivity.this, CustomersActivity.class);
                startActivity(customerIntent);
                return true;
            }
            return false;
        });


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        stockAdapter = new StockAdapter(this, new ArrayList<>(), new StockAdapter.OnStockUpdateListener() {
            @Override
            public void onStockUpdate(Product product, int newStockAmount) {
                updateProductStock(product, newStockAmount);
            }
        });
        recyclerView.setAdapter(stockAdapter);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        productRepository = new ProductRepository();

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                stockAdapter.getFilter().filter(newText);
                return true;
            }
        });

        fetchProducts();
    }

    private void fetchProducts() {
        productRepository.getProducts(new ProductRepository.DataStatus() {
            @Override
            public void DataIsLoaded(List<Product> products, List<String> keys) {
                stockAdapter.setItems(products);
                stockAdapter.notifyDataSetChanged();
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }

            @Override
            public void StockLevelLoaded(int stockLevel) {

            }

            @Override
            public void DataLoadFailed(DatabaseError databaseError) {
                Toast.makeText(StockActivity.this, "Failed to load products: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateProductStock(Product product, int newStockAmount) {
        productRepository.updateProductStock(product.getProductId(), newStockAmount);
    }
}