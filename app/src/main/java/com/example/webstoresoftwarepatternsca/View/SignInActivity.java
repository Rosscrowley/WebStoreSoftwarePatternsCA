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
import com.example.webstoresoftwarepatternsca.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        // Initialize FirebaseAuth instance
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
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginActivity", "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser(); // Correct use of mAuth
                    // Navigate to next screen or show success message
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // If sign in fails, display a message to the user
                    Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}