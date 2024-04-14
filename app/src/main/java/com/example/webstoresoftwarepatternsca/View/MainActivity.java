package com.example.webstoresoftwarepatternsca.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.webstoresoftwarepatternsca.Model.Comment;
import com.example.webstoresoftwarepatternsca.Model.CommentsRepository;
import com.example.webstoresoftwarepatternsca.Model.Product;
import com.example.webstoresoftwarepatternsca.Model.ProductRepository;
import com.example.webstoresoftwarepatternsca.R;
import com.example.webstoresoftwarepatternsca.ViewModel.AlphabeticalSortStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.CategoryFilterStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.FeatureSortStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.FilterStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.FilterStrategyFactory;
import com.example.webstoresoftwarepatternsca.ViewModel.ManufacturerFilterStrategy;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> originalList = new ArrayList<>();
    private ProductRepository productRepository = new ProductRepository();
    private FilterStrategy currentFilterStrategy;
    private Spinner filterSpinner;

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
                for (final Product product : products) {
                    CommentsRepository commentsRepository = new CommentsRepository(product.getProductId());
                    commentsRepository.calculateAverageRating(new CommentsRepository.DataStatus() {
                        @Override
                        public void AverageRatingLoaded(float averageRating) {
                            product.setAverageRating(averageRating);
                            productAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void DataIsLoaded(List<Comment> comments) {

                        }

                        @Override
                        public void DataLoadFailed(DatabaseError databaseError) {
                            Log.e("MainActivity", "Failed to load ratings: " + databaseError.getMessage());
                        }
                    });
                }

                productList.clear();
                productList.addAll(products);
                originalList = new ArrayList<>(productList);
                productAdapter.notifyDataSetChanged();
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
            @Override
            public void StockLevelLoaded(int stockLevel) {}
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

        filterSpinner = findViewById(R.id.spinner);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();

                if ("Clear".equals(selectedOption)) {
                    Log.d("MainActivity", "Clear filter selected, displaying all products...");
                    resetFilter();
                } else if ("Category".equals(selectedOption)) {
                    // Fetch and show categories
                    productRepository.getLiveCategories().observe(MainActivity.this, categories -> {
                        if (categories != null && !categories.isEmpty()) {
                            showFilterDialog("Select Categories", categories, selectedCategories ->
                                    onFilterSelected("Category", selectedCategories));
                        }
                    });
                } else if ("Manufacturer".equals(selectedOption)) {
                    // Fetch and show manufacturers
                    productRepository.getLiveManufacturers().observe(MainActivity.this, manufacturers -> {
                        if (manufacturers != null && !manufacturers.isEmpty()) {
                            showFilterDialog("Select Manufacturers", manufacturers, selectedManufacturers ->
                                    onFilterSelected("Manufacturer", selectedManufacturers));
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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

    private void onFilterSelected(String filterType, Set<String> criteria) {
        productList.clear();
        productList.addAll(originalList);

        if (!criteria.isEmpty()) {
            currentFilterStrategy = FilterStrategyFactory.getStrategy(filterType);
            List<Product> filteredProducts = currentFilterStrategy.filter(productList, criteria);

            productList.clear();
            productList.addAll(filteredProducts);
        }
        productAdapter.notifyDataSetChanged();
    }

    private void showFilterDialog(String title, Set<String> items, Consumer<Set<String>> onSelection) {
        final CharSequence[] itemArray = items.toArray(new CharSequence[0]);
        final boolean[] checkedItems = new boolean[itemArray.length];
        final Set<String> selectedItems = new HashSet<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        Arrays.fill(checkedItems, false);

        builder.setMultiChoiceItems(itemArray, checkedItems, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedItems.add(itemArray[which].toString());
            } else {
                selectedItems.remove(itemArray[which].toString());
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> onSelection.accept(selectedItems));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void resetFilter() {
        productList.clear();
        productList.addAll(originalList);
        productAdapter.notifyDataSetChanged();
    }
}

