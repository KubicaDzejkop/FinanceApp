package com.example.financeapp.ui.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private String recipient;
    private double amount;
    private String category;
    private String date; // Format: YYYY-MM-DD
    private String type; // "income" lub "expense"

    // Konstruktor
    public Transaction(int userId, String recipient, double amount, String category, String date, String type) {
        this.recipient = recipient;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.type = type;
        this.userId = userId;
    }

    // Gettery i settery
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}