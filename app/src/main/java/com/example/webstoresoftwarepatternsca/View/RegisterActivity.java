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
import com.example.webstoresoftwarepatternsca.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText, adminCodeEditText;
    private Button signupButton;
    private TextView switchToSignIn;
    private AuthenticationRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthenticationRepository();

        emailEditText = findViewById(R.id.emailEntered);
        passwordEditText = findViewById(R.id.passEntered);
        confirmPasswordEditText = findViewById(R.id.confirmPassEntered);
        switchToSignIn = findViewById(R.id.textView);
        adminCodeEditText = findViewById(R.id.adminCodeTextView);

        switchToSignIn.setOnClickListener(view -> {
            Intent i = new Intent(RegisterActivity.this, SignInActivity.class);
            startActivity(i);
        });

        signupButton = findViewById(R.id.button);

        signupButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String adminCode = adminCodeEditText.getText().toString().trim();



            // Check if the admin code is entered and correct
            if (!adminCode.isEmpty() && adminCode.equals("0939")) {
                // User is admin
                authRepository.registerUser(email, password, task -> {
                    if (task.isSuccessful()) {
                        // Sign up successful
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (user != null) {

                            Log.d("RegisterActivity", "Admin code entered. Marking user as admin...");
                            User newUser = new User(user.getUid(), email, null, null, null, "No Tier", true);
                            UserRepository userRepository = new UserRepository();
                            userRepository.addUser(newUser);

                            Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {

                authRepository.registerUser(email, password, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
