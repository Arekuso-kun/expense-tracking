package com.example.expensetracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {
    private TextInputEditText emailInput, passwordInput, confirmPasswordInput;
    private Button signupButton;
    private Button goToLoginButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        signupButton = findViewById(R.id.createAccountButton);
        goToLoginButton = findViewById(R.id.goToLoginButton);

        firebaseAuth = FirebaseAuth.getInstance();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        goToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void createAccount() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Completeaza toate campurile!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Parolele nu se potrivesc!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Parola trebuie să aiba cel putin 6 caractere!", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                firebaseAuth.getCurrentUser().sendEmailVerification()
                        .addOnCompleteListener(verificationTask -> {
                            if (verificationTask.isSuccessful()) {
                                Toast.makeText(SignupActivity.this, "Cont creat cu succes! Verificati email-ul pentru activare.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SignupActivity.this, "Eroare la trimiterea email-ului de verificare.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(SignupActivity.this, "Eroare la creare cont: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}