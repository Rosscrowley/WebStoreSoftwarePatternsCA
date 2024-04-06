package com.example.webstoresoftwarepatternsca.Model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private Map<String, CartItem> cartItems = new HashMap<>();
    private final MutableLiveData<Integer> itemCount = new MutableLiveData<>();
    private CartRepository cartRepository;

    private CartManager() {
        cartRepository = new CartRepository();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItemToCart(String productId, int quantity) {
        // Check if the product is already in the cart
        if (cartItems.containsKey(productId)) {
            CartItem existingItem = cartItems.get(productId);
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            cartItems.put(productId, new CartItem(productId, quantity));
        }
        updateItemCount();
        saveCartToFirebase();
    }

    private void updateItemCount() {
        int totalItems = cartItems.values().stream().mapToInt(CartItem::getQuantity).sum();
        itemCount.postValue(totalItems);
    }

    public LiveData<Integer> getItemCount() {
        return itemCount;
    }

    private void saveCartToFirebase() {
        String userId = getCurrentUserId();
        if (userId != null) {
            cartRepository.saveCartItems(userId, new ArrayList<>(cartItems.values()));
        }
    }
    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }
}
