package com.example.webstoresoftwarepatternsca.View;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webstoresoftwarepatternsca.Model.AuthenticationRepository;
import com.example.webstoresoftwarepatternsca.Model.User;
import com.example.webstoresoftwarepatternsca.Model.UserRepository;
import com.example.webstoresoftwarepatternsca.Model.UserSessionManager;
import com.example.webstoresoftwarepatternsca.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public class SignInActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView switchToRegister;
    private AuthenticationRepository authRepository;
    private FirebaseAuth mAuth; // Declaration of the FirebaseAuth instance
    private UserRepository userRepository; // Added repository for user operations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        authRepository = new AuthenticationRepository();
        userRepository = new UserRepository(); // Initialize the UserRepository

        emailEditText = findViewById(R.id.emailEntered);
        passwordEditText = findViewById(R.id.passEntered);
        loginButton = findViewById(R.id.signInButton);
        switchToRegister = findViewById(R.id.textView);

        switchToRegister.setOnClickListener(view -> {
            Intent i = new Intent(SignInActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                authRepository.signInUser(email, password, task -> {
                    if (task.isSuccessful()) {
                        Log.d("SignInActivity", "signInWithEmail:success");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            userRepository.initializeNewUserAfterSignIn(firebaseUser.getUid(), firebaseUser.getEmail(), new UserRepository.UserFetchListener() {
                                @Override
                                public void onUserFetched(User user) {
                                    UserSessionManager.getInstance().initializeSessionWithFirebaseUserId(user.getUserId(), new UserSessionManager.UserFetchListener() {
                                        @Override
                                        public void onUserFetched(User user) {
                                            navigateToMainActivity();
                                        }

                                        @Override
                                        public void onError(DatabaseError error) {
                                            Log.e("SignInActivity", "Error initializing session: " + error.getMessage());
                                            Toast.makeText(SignInActivity.this, "Session initialization failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onError(DatabaseError error) {
                                    Log.e("SignInActivity", "Error fetching user: " + error.getMessage());
                                    Toast.makeText(SignInActivity.this, "Error fetching user details.", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onUsersFetched(List<User> userList) {
                                    // Not used in this context
                                }
                            });
                        }
                    } else {
                        Log.w("SignInActivity", "signInWithEmail:failure", task.getException());
                        Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(SignInActivity.this, "Please enter email and password.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}