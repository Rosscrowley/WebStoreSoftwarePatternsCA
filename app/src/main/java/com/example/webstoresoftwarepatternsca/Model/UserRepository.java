package com.example.webstoresoftwarepatternsca.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserRepository {
    private DatabaseReference databaseReference;

    public UserRepository() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    public void addUser(User user) {
        databaseReference.child(user.getUserId()).setValue(user);
    }

}