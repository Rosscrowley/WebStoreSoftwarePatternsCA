package com.example.webstoresoftwarepatternsca.Model;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthenticationRepository {

    private FirebaseAuth mAuth;

    public AuthenticationRepository() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void registerUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

    public void signInUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }
}
