package com.example.expensetracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ExpenseActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "com.example.expensetracking.EXTRA_TITLE";
    public static final String EXTRA_AMOUNT = "com.example.expensetracking.EXTRA_AMOUNT";
    public static final String EXTRA_POSITION = "com.example.expensetracking.EXTRA_POSITION";

    private EditText titleInput;
    private EditText amountInput;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        titleInput = findViewById(R.id.titleInput);
        amountInput = findViewById(R.id.amountInput);
        saveButton = findViewById(R.id.saveButton);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_TITLE) && intent.hasExtra(EXTRA_AMOUNT)) {
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
                resultIntent.putExtra(EXTRA_TITLE, title);
                resultIntent.putExtra(EXTRA_AMOUNT, amount);

                if (getIntent().hasExtra(EXTRA_POSITION)) {
                    resultIntent.putExtra(EXTRA_POSITION, getIntent().getIntExtra(EXTRA_POSITION, -1));
                }

                setResult(RESULT_OK, resultIntent);
                finish();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Suma nu este valida", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Completeaza toate campurile", Toast.LENGTH_SHORT).show();
        }
    }
}
