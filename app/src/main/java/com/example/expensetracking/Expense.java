package com.example.expensetracking;

import java.util.Date;

public class Expense {
    private String id;
    private String title;
    private double amount;
    private Date creationDate;

    public Expense() {}

    public Expense(String title, double amount) {
        this.title = title;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
