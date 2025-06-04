package com.example.financeapp.ui.models;

public class MonthlyExpense {
    public String month;
    public double total;

    public MonthlyExpense(String month, double total) {
        this.month = month;
        this.total = total;
    }
}