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

public class SignInActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView switchToRegister;
    private AuthenticationRepository authRepository;
    private FirebaseAuth mAuth; // Declaration of the FirebaseAuth instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        authRepository = new AuthenticationRepository();

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
            // Validate inputs before attempting to log in

            authRepository.signInUser(email, password, task -> {
                if (task.isSuccessful()) {
                    Log.d("LoginActivity", "signInWithEmail:success");
                    FirebaseUser firebaseUser = mAuth.getCurrentUser(); // Get the Firebase user

                    if (firebaseUser != null) {
                        String userId = firebaseUser.getUid(); // Get Firebase user ID
                        UserRepository userRepository = new UserRepository();

                        userRepository.fetchUserById(userId, new UserRepository.UserFetchListener() {
                            @Override
                            public void onUserFetched(User user) {
                                if (user == null) {
                                    User newUser = new User(userId, firebaseUser.getEmail(), null, null, null);
                                    userRepository.addUser(newUser); // Save the new user to the database
                                }
                                UserSessionManager.getInstance().initializeSessionWithFirebaseUserId(userId);
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onError(DatabaseError error) {

                            }
                        });
                    }
                } else {
                    Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                    Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}