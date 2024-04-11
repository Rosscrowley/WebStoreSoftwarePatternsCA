package com.example.webstoresoftwarepatternsca.Model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

            }
        });

        return liveCartItems;
    }

    public void addItemToUserCart(String userId, CartItem cartItem) {
        // Directly save the CartItem under the user's cart.
        // This will create or update the cartItem based on productId
        cartsRef.child(userId).child(cartItem.getProductId()).setValue(cartItem);
    }

    public void removeOneItemFromCart(String userId, String productId) {
        DatabaseReference cartItemRef = cartsRef.child(userId).child(productId);

        cartItemRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                CartItem cartItem = mutableData.getValue(CartItem.class);
                if (cartItem == null) {
                    return Transaction.success(mutableData);
                }

                if (cartItem.getQuantity() > 1) {
                    // Decrement the quantity by one
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                    mutableData.setValue(cartItem);
                } else {
                    // Remove the item if the quantity becomes 0
                    mutableData.setValue(null);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                Log.d("CartRepo", "removeOneItemFromCart:onComplete:" + databaseError);
            }
        });
    }

    public void saveCartItems(String userId, ArrayList<CartItem> cartItems) {

        Map<String, Object> cartItemsToUpdate = new HashMap<>();
        for (CartItem item : cartItems) {
            cartItemsToUpdate.put(item.getProductId(), item.toMap());
        }

        for (Map.Entry<String, Object> entry : cartItemsToUpdate.entrySet()) {
            Log.d("CartRepository", "Key: " + entry.getKey() + " Value: " + entry.getValue().toString());
        }

        cartsRef.child(userId).updateChildren(cartItemsToUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    public void clearUserCart(String userId) {
        cartsRef.child(userId).removeValue()
                .addOnSuccessListener(aVoid -> Log.d("CartRepository", "User cart cleared successfully."))
                .addOnFailureListener(e -> Log.e("CartRepository", "Failed to clear user cart.", e));
    }
}
