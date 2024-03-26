package com.example.webstoresoftwarepatternsca.View;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webstoresoftwarepatternsca.Model.AuthenticationRepository;
import com.example.webstoresoftwarepatternsca.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPassword;
    private Button signupButton;
    private AuthenticationRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthenticationRepository();

        emailEditText = findViewById(R.id.emailEntered);
        passwordEditText = findViewById(R.id.passEntered);
        confirmPassword = findViewById(R.id.confirmPassEntered);

        signupButton = findViewById(R.id.button);

        signupButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            // Validate inputs

            authRepository.registerUser(email, password, task -> {
                if (task.isSuccessful()) {
                    // Sign up successful
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                    startActivity(intent);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
