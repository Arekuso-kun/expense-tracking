package com.example.expensetracking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends ArrayAdapter<Expense> {

    private Context context;
    private List<Expense> expenses;
    private SimpleDateFormat dateFormat;

    public ExpenseAdapter(Context context, List<Expense> expenses) {
        super(context, 0, expenses);
        this.context = context;
        this.expenses = expenses;
        this.dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("ro", "RO")); // Romanian locale
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_expense, parent, false);
        }

        Expense expense = expenses.get(position);

        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView amountTextView = convertView.findViewById(R.id.amountTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);

        titleTextView.setText(expense.getTitle());

        String formattedAmount = String.format("%.2f RON", expense.getAmount());
        String formattedDate = expense.getCreationDate() != null ? dateFormat.format(expense.getCreationDate()) : "N/A";

        amountTextView.setText(formattedAmount);
        dateTextView.setText(formattedDate);

        return convertView;
    }

}
