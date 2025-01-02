package com.example.expensetracking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailInput, passwordInput;
    private Button loginButton;
    private SharedPreferences sharedPreferences;

    // Utilizatori hardcodați
    private final HashMap<String, String> users = new HashMap<String, String>() {{
        put("test1@example.com", "parola1");
        put("test2@example.com", "parola2");
        put("admin@example.com", "admin123");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        sharedPreferences = getSharedPreferences("UserAuth", MODE_PRIVATE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Introduceti email-ul si parola!", Toast.LENGTH_SHORT).show();
                } else {
                    authenticateUser(email, password);
                }
            }
        });

        checkSavedCredentials();
    }

    private void authenticateUser(String email, String password) {
        if (users.containsKey(email) && users.get(email).equals(password)) {
            saveCredentials(email);

            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Email sau parola incorecta!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCredentials(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.apply();
    }

    private void checkSavedCredentials() {
        String savedEmail = sharedPreferences.getString("email", null);
        if (savedEmail != null) {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
