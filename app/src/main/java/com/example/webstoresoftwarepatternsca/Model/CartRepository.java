package com.example.webstoresoftwarepatternsca.Model;
import android.util.Log;

import androidx.annotation.NonNull;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartRepository {

    private DatabaseReference cartsRef = FirebaseDatabase.getInstance("https://softwarepatternsca-f1030-default-rtdb.europe-west1.firebasedatabase.app").getReference("carts");

    public interface UpdateCartCallback {
        void onComplete();
    }

    public LiveData<List<CartItem>> getCartItems(String userId) {
        MutableLiveData<List<CartItem>> liveCartItems = new MutableLiveData<>();
        cartsRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<CartItem> cartItems = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CartItem item = snapshot.getValue(CartItem.class);
                    if (item != null) {
                        cartItems.add(item);
                    }
                }
                liveCartItems.setValue(cartItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log cancellation or handle it as needed
            }
        });
        return liveCartItems;
    }

    public DatabaseReference getProductReference(String userId, String productId) {
        return cartsRef.child("carts").child(userId).child(productId);
    }

    public void addItemToUserCart(String userId, CartItem cartItem) {
        cartsRef.child(userId).child(cartItem.getProductId()).setValue(cartItem);
    }

    public void removeOneItemFromCart(String userId, String productId, UpdateCartCallback callback) {
        DatabaseReference cartItemRef = cartsRef.child(userId).child(productId);
        cartItemRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                CartItem cartItem = mutableData.getValue(CartItem.class);
                if (cartItem != null && cartItem.getQuantity() > 1) {
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                    mutableData.setValue(cartItem);
                } else {
                    mutableData.setValue(null); // Remove the item completely if quantity is 1 or less
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (committed) {
                    callback.onComplete(); // Notify the callback when the transaction is complete
                }
            }
        });
    }

    public void saveCartItems(String userId, ArrayList<CartItem> cartItems) {
        Map<String, Object> cartItemsToUpdate = new HashMap<>();
        for (CartItem item : cartItems) {
            cartItemsToUpdate.put(item.getProductId(), item.toMap());
        }
        cartsRef.child(userId).updateChildren(cartItemsToUpdate)
                .addOnSuccessListener(aVoid -> Log.d("CartRepository", "Cart items updated successfully."))
                .addOnFailureListener(e -> Log.e("CartRepository", "Failed to update cart items.", e));
    }

    public void clearUserCart(String userId) {
        cartsRef.child(userId).removeValue()
                .addOnSuccessListener(aVoid -> Log.d("CartRepository", "User cart cleared successfully."))
                .addOnFailureListener(e -> Log.e("CartRepository", "Failed to clear user cart.", e));
    }
}