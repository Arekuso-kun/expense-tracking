package com.example.expensetracking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private ListView expensesListView;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenses = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private int selectedExpensePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        expensesListView = findViewById(R.id.expensesListView);
        expenseAdapter = new ExpenseAdapter(this, expenses);
        expensesListView.setAdapter(expenseAdapter);

        registerForContextMenu(expensesListView);

        user = firebaseAuth.getCurrentUser();
        Toast.makeText(this, "Bine ai revenit, " + user.getEmail() + "!", Toast.LENGTH_SHORT).show();

        loadExpenses();

        expensesListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedExpensePosition = position;
        });
    }

    private void loadExpenses() {
        Log.d("ExpenseActivity", "UID: " + user.getUid());
        db.collection("users").document(user.getUid())
                .collection("expenses").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                expenses.clear();
                for (DocumentSnapshot doc : task.getResult()) {
                    Expense expense = doc.toObject(Expense.class);
                    expense.setId(doc.getId());
                    expenses.add(expense);
                }
                expenseAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Eroare la încarcarea cheltuielilor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();

        if (selectedItem == R.id.action_add) {
            showAddExpenseDialog();
        } else if (selectedItem == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAddExpenseDialog() {
        Intent intent = new Intent(this, ExpenseActivity.class);
        intent.putExtra(ExpenseActivity.EXTRA_UID, user.getUid());
        startActivityForResult(intent, 1);
    }

    private void showEditExpenseDialog(Expense expense) {
        Intent intent = new Intent(this, ExpenseActivity.class);
        intent.putExtra(ExpenseActivity.EXTRA_ID, expense.getId());
        intent.putExtra(ExpenseActivity.EXTRA_UID, user.getUid());
        intent.putExtra(ExpenseActivity.EXTRA_TITLE, expense.getTitle());
        intent.putExtra(ExpenseActivity.EXTRA_AMOUNT, expense.getAmount());
        startActivityForResult(intent, 2);
    }

    private void deleteExpense() {
        String expenseId = expenses.get(selectedExpensePosition).getId();
        db.collection("users").document(user.getUid())
                .collection("expenses").document(expenseId).delete()
                .addOnSuccessListener(aVoid -> {
                    expenses.remove(selectedExpensePosition);
                    expenseAdapter.notifyDataSetChanged();
                    selectedExpensePosition = -1;
                    Toast.makeText(this, "Cheltuiala a fost ștearsa!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Eroare la stergere!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_expense, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        selectedExpensePosition = info.position;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();

        if (selectedItem == R.id.context_edit && selectedExpensePosition != -1) {
            showEditExpenseDialog(expenses.get(selectedExpensePosition));
        } else if (selectedItem == R.id.context_delete) {
            deleteExpense();
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            loadExpenses();
        }
    }
}
