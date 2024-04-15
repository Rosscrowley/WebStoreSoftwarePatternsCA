package com.example.webstoresoftwarepatternsca.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.webstoresoftwarepatternsca.ViewModel.GoldState;
import com.example.webstoresoftwarepatternsca.ViewModel.NoTierState;
import com.example.webstoresoftwarepatternsca.ViewModel.PlatinumState;
import com.example.webstoresoftwarepatternsca.ViewModel.SilverState;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private DatabaseReference databaseReference;

    public UserRepository() {
        databaseReference = FirebaseDatabase.getInstance("https://softwarepatternsca-f1030-default-rtdb.europe-west1.firebasedatabase.app").getReference("users");
    }

    public void addUser(User user) {
        databaseReference.child(user.getUserId()).setValue(user);
    }

    public void updateUserId(String userId) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // User doesn't exist, so create a basic profile
                    User newUser = new User(userId);
                    databaseReference.child(userId).setValue(newUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("UserRepository", "Failed to read value.", error.toException());
            }
        });
    }

    public void fetchUserById(String userId, UserFetchListener listener) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        listener.onUserFetched(user);
                    }
                } else {
                    // User does not exist, create a new user profile
                    User newUser = new User(userId, "email@example.com", null, null, null, "No Tier");
                    addUser(newUser);
                    listener.onUserFetched(newUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error);
            }
        });
    }

    public interface UserFetchListener {
        void onUserFetched(User user);
        void onError(DatabaseError error);
    }

    public void updateUserCardDetails(String userId, CardDetail cardDetail) {
        Map<String, Object> cardDetailsMap = new HashMap<>();
        cardDetailsMap.put("cardNumber", cardDetail.getCardNumber());
        cardDetailsMap.put("expiryDate", cardDetail.getExpiryDate());
        cardDetailsMap.put("cvv", cardDetail.getCvv());

        databaseReference.child(userId).child("cardDetail").updateChildren(cardDetailsMap)
                .addOnSuccessListener(aVoid -> Log.d("UserRepository", "Card details updated successfully."))
                .addOnFailureListener(e -> Log.e("UserRepository", "Failed to update card details.", e));
    }

    public void updateUserShippingAddress(String userId, ShippingAddress shippingAddress) {
        Map<String, Object> shippingAddressMap = new HashMap<>();
        shippingAddressMap.put("address", shippingAddress.getAddress());
        shippingAddressMap.put("city", shippingAddress.getCity());
        shippingAddressMap.put("postalCode", shippingAddress.getPostalCode());

        databaseReference.child(userId).child("shippingAddress").updateChildren(shippingAddressMap)
                .addOnSuccessListener(aVoid -> Log.d("UserRepository", "Shipping address updated successfully."))
                .addOnFailureListener(e -> Log.e("UserRepository", "Failed to update shipping address.", e));
    }

    public void updateUserSpendingAndLoyalty(String userId, double amountSpent, UserFetchListener listener) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        // Update the user's total spent
                        user.addSpending(amountSpent);


                        databaseReference.child(userId).setValue(user)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("UserRepository", "User spending and loyalty updated successfully.");
                                    listener.onUserFetched(user);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("UserRepository", "Failed to update user spending and loyalty.", e);
                                });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserRepository", "Failed to fetch user for spending update.", error.toException());
            }
        });
    }

//    private void updateLoyaltyState(User user) {
//        if (user.getTotalSpent() >= 500) {
//            user.setLoyaltyState(new PlatinumState());
//            user.setLoyaltyTier("Platinum");
//        } else if (user.getTotalSpent() >= 250) {
//            user.setLoyaltyState(new GoldState());
//            user.setLoyaltyTier("Gold");
//        } else if (user.getTotalSpent() >= 100) {
//            user.setLoyaltyState(new SilverState());
//            user.setLoyaltyTier("Silver");
//        } else {
//            user.setLoyaltyState(new NoTierState());
//            user.setLoyaltyTier("No Tier");
//        }
//        // After updating the state, save the changes
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("totalSpent", user.getTotalSpent());
//        updates.put("loyaltyTier", user.getLoyaltyTier());
//
//        databaseReference.child(user.getUserId()).updateChildren(updates);
//    }
}