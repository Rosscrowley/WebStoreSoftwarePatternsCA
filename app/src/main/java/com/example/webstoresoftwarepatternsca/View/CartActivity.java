package com.example.webstoresoftwarepatternsca.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webstoresoftwarepatternsca.Model.CartItem;
import com.example.webstoresoftwarepatternsca.Model.CartRepository;
import com.example.webstoresoftwarepatternsca.Model.Product;
import com.example.webstoresoftwarepatternsca.Model.ProductRepository;
import com.example.webstoresoftwarepatternsca.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartItemAdapter adapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private Map<String, Product> productMap = new HashMap<>();
    private ProductRepository productRepository;
    private CartRepository cartRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRepository = new CartRepository();
        productRepository = new ProductRepository();

        ImageButton closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        populateCartItems();

        adapter = new CartItemAdapter(this, cartItems, productMap, new CartItemAdapter.OnItemRemoveListener() {
            @Override
            public void onItemRemoved(CartItem cartItem) {


                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String productId = cartItem.getProductId();
                cartRepository.removeOneItemFromCart(currentUserId, productId);
                cartItems.remove(cartItem);
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(adapter);

        Button checkoutButton = findViewById(R.id.CheckoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the checkout process
                goToCheckout();
            }
        });
    }

    private void populateCartItems() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartRepository.getCartItems(currentUserId).observe(this, new Observer<List<CartItem>>() {
            @Override
            public void onChanged(List<CartItem> newCartItems) {
                for (CartItem cartItem : newCartItems) {
                    String productId = cartItem.getProductId();

                    productRepository.getProduct(productId, new ProductRepository.DataStatus() {
                        @Override
                        public void DataIsLoaded(List<Product> products, List<String> keys) {
                            for (Product product : products) {
                                productMap.put(product.getProductId(), product);
                            }
                            cartItems.clear();
                            cartItems.addAll(newCartItems);
                            adapter.notifyDataSetChanged();
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
                        public void DataLoadFailed(DatabaseError databaseError) {

                        }
                        @Override
                        public void StockLevelLoaded(int stockLevel) {}
                    });
                }
            }
        });
    }
    private void goToCheckout() {
        double totalPrice = calculateTotalPrice(cartItems, productMap);
        Intent checkoutIntent = new Intent(CartActivity.this, CheckoutActivity.class);
        checkoutIntent.putParcelableArrayListExtra("cartItems", (ArrayList<? extends Parcelable>) cartItems); // Assuming cartItems is List<CartItem>
        checkoutIntent.putExtra("totalPrice", totalPrice);
        startActivity(checkoutIntent);
    }

    private double calculateTotalPrice(List<CartItem> cartItems, Map<String, Product> productMap) {
        double total = 0;
        for (CartItem item : cartItems) {
            Product product = productMap.get(item.getProductId());
            if (product != null) {
                total += product.getPrice() * item.getQuantity();
            }
        }
        return total;
    }
}