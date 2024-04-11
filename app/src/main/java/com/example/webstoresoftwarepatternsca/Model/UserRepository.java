package com.example.webstoresoftwarepatternsca.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        Log.d("UserRepository", "Fetching user details for ID: " + userId);
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        Log.d("UserRepository", "User fetched successfully: " + user.toString());
                        listener.onUserFetched(user);
                    } else {
                        Log.d("UserRepository", "Failed to deserialize user.");
                    }
                } else {
                    Log.d("UserRepository", "User snapshot does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserRepository", "Error fetching user: " + error.getMessage());
                listener.onError(error);
            }
        });
    }

    public interface UserFetchListener {
        void onUserFetched(User user);
        void onError(DatabaseError error);
    }


}