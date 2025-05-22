package com.example.financeapp.ui.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String recipient;
    private double amount;
    private String category;
    private String date;
    private String type; // "income" lub "expense"

    // Konstruktor
    public Transaction(String recipient, double amount, String category, String date, String type) {
        this.recipient = recipient;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.type = type;
    }

    // Gettery i settery (Room wymaga setterów dla pól)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String title) { this.recipient = recipient; }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }
}