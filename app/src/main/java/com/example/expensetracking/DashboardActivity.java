package com.example.expensetracking;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private ListView expensesListView;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenses = new ArrayList<>();
    private int selectedExpensePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        expensesListView = findViewById(R.id.expensesListView);
        expenseAdapter = new ExpenseAdapter(this, expenses);
        expensesListView.setAdapter(expenseAdapter);

        registerForContextMenu(expensesListView);

        String savedEmail = getSharedPreferences("UserAuth", MODE_PRIVATE).getString("email", "Unknown");
        Toast.makeText(this, "Bine ai revenit, " + savedEmail + "!", Toast.LENGTH_SHORT).show();

        expensesListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedExpensePosition = position;
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

            getSharedPreferences("UserAuth", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAddExpenseDialog() {
        Intent intent = new Intent(this, ExpenseActivity.class);
        startActivityForResult(intent, 1);
    }

    private void showEditExpenseDialog(Expense expense) {
        Intent intent = new Intent(this, ExpenseActivity.class);
        intent.putExtra(ExpenseActivity.EXTRA_TITLE, expense.getTitle());
        intent.putExtra(ExpenseActivity.EXTRA_AMOUNT, expense.getAmount());
        intent.putExtra(ExpenseActivity.EXTRA_POSITION, selectedExpensePosition);
        startActivityForResult(intent, 2);
    }

    private void deleteExpense() {
        expenses.remove(selectedExpensePosition);
        expenseAdapter.notifyDataSetChanged();
        selectedExpensePosition = -1;
        Toast.makeText(this, "Cheltuiala a fost stearsa!", Toast.LENGTH_SHORT).show();
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
            String title = data.getStringExtra(ExpenseActivity.EXTRA_TITLE);
            double amount = data.getDoubleExtra(ExpenseActivity.EXTRA_AMOUNT, 0);

            if (requestCode == 1) {
                // Add expense
                expenses.add(new Expense(title, amount));
            } else if (requestCode == 2) {
                // Edit expense
                int position = data.getIntExtra(ExpenseActivity.EXTRA_POSITION, -1);
                if (position != -1) {
                    Expense expense = expenses.get(position);
                    expense.setTitle(title);
                    expense.setAmount(amount);
                }
            }

            expenseAdapter.notifyDataSetChanged();
        }
    }
}
