package com.example.expensetracking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ExpenseAdapter extends ArrayAdapter<Expense> {

    private Context context;
    private List<Expense> expenses;

    public ExpenseAdapter(Context context, List<Expense> expenses) {
        super(context, 0, expenses);
        this.context = context;
        this.expenses = expenses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        Expense expense = expenses.get(position);

        TextView titleTextView = convertView.findViewById(android.R.id.text1);
        TextView amountTextView = convertView.findViewById(android.R.id.text2);

        titleTextView.setText(expense.getTitle());
        amountTextView.setText(String.format("%.2f RON", expense.getAmount()));

        return convertView;
    }
}
