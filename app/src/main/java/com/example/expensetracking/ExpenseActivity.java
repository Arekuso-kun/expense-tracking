package com.example.expensetracking;

import java.util.Map;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ExpenseActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.example.expensetracking.EXTRA_ID";
    public static final String EXTRA_UID = "com.example.expensetracking.EXTRA_UID";
    public static final String EXTRA_TITLE = "com.example.expensetracking.EXTRA_TITLE";
    public static final String EXTRA_AMOUNT = "com.example.expensetracking.EXTRA_AMOUNT";

    private EditText titleInput;
    private EditText amountInput;
    private Button saveButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        db = FirebaseFirestore.getInstance();

        titleInput = findViewById(R.id.titleInput);
        amountInput = findViewById(R.id.amountInput);
        saveButton = findViewById(R.id.saveButton);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            titleInput.setText(intent.getStringExtra(EXTRA_TITLE));
            amountInput.setText(String.valueOf(intent.getDoubleExtra(EXTRA_AMOUNT, 0)));
            saveButton.setText(R.string.expense_edit);
        } else {
            saveButton.setText(R.string.expense_add);
        }
    }

    public void onSaveExpense(View view) {
        String title = titleInput.getText().toString();
        String amountString = amountInput.getText().toString();

        if (!title.isEmpty() && !amountString.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountString);
                Intent resultIntent = new Intent();

                String uid = getIntent().getStringExtra(EXTRA_UID);

                if (getIntent().hasExtra(EXTRA_ID)) {
                    String id = getIntent().getStringExtra(EXTRA_ID);

                    db.collection("users").document(uid)
                            .collection("expenses").document(id)
                            .update("title", title, "amount", amount)
                            .addOnSuccessListener(aVoid -> {
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            });
                } else {
                    Map<String, Object> expenseData = new HashMap<>();
                    expenseData.put("title", title);
                    expenseData.put("amount", amount);

                    db.collection("users").document(uid)
                            .collection("expenses")
                            .add(expenseData)
                            .addOnSuccessListener(documentReference -> {
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            });
                }

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Suma nu este valida", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Completeaza toate campurile", Toast.LENGTH_SHORT).show();
        }
    }
}
