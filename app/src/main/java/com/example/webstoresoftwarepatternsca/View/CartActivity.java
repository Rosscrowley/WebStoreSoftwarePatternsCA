package com.example.webstoresoftwarepatternsca.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
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
import java.util.Locale;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartItemAdapter adapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private Map<String, Product> productMap = new HashMap<>();
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private TextView totalPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRepository = new CartRepository();
        productRepository = new ProductRepository();

        totalPrice = findViewById(R.id.price);

        ImageButton closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CartItemAdapter(this, cartItems, productMap, new CartItemAdapter.OnItemRemoveListener() {
            @Override
            public void onItemRemoved(CartItem cartItem, int position) {
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                cartRepository.removeOneItemFromCart(currentUserId, cartItem.getProductId(), new CartRepository.UpdateCartCallback() {

                    @Override
                    public void onComplete() {
                        runOnUiThread(() -> {
                            if (position < cartItems.size() && position >= 0) {
                                CartItem currentCartItem = cartItems.get(position);
                                if (currentCartItem.getProductId().equals(cartItem.getProductId())) {
                                    if (currentCartItem.getQuantity() <= 1) {
                                        cartItems.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        if (cartItems.isEmpty()) {
                                            // Handle empty cart scenario, maybe show a message or disable checkout
                                        }
                                    } else {
                                        currentCartItem.setQuantity(currentCartItem.getQuantity() - 1);
                                        adapter.notifyItemChanged(position);
                                    }
                                    updateTotalPrice();
                                } else {
                                    Log.e("CartItemAdapter", "Error: The cart item at position has changed");
                                }
                            } else {
                                Log.e("CartItemAdapter", "Error: The position " + position + " is out of cart items list bounds, list size: " + cartItems.size());
                            }
                        });
                    }
                });
            }
        });

        recyclerView.setAdapter(adapter);
        populateCartItems();

        Button checkoutButton = findViewById(R.id.CheckoutButton);
        checkoutButton.setOnClickListener(v -> goToCheckout());
    }

    private void populateCartItems() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartRepository.getCartItems(currentUserId).observe(this, newCartItems -> {
            productMap.clear();
            cartItems.clear();
            cartItems.addAll(newCartItems);

            for (CartItem cartItem : newCartItems) {
                String productId = cartItem.getProductId();
                productRepository.getProduct(productId, new ProductRepository.DataStatus() {
                    @Override
                    public void DataIsLoaded(List<Product> products, List<String> keys) {
                        for (Product product : products) {
                            productMap.put(product.getProductId(), product);
                        }
                        adapter.notifyDataSetChanged();
                        updateTotalPrice();
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
                    public void StockLevelLoaded(int stockLevel) {

                    }
                });
            }
        });
    }

    private void updateTotalPrice() {
        double total = calculateTotalPrice(cartItems, productMap);
        totalPrice.setText(String.format(Locale.US, "€%.2f", total));
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

    private void goToCheckout() {
        double total = calculateTotalPrice(cartItems, productMap);
        Intent checkoutIntent = new Intent(CartActivity.this, CheckoutActivity.class);
        checkoutIntent.putParcelableArrayListExtra("cartItems", new ArrayList<>(cartItems));
        checkoutIntent.putExtra("totalPrice", total);
        startActivity(checkoutIntent);
    }
}