package com.example.financeapp.ui.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categorylimit")
public class CategoryLimit {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String userId;
    public String category;
    public double limitAmount;

    public CategoryLimit(String userId, String category, double limitAmount) {
        this.userId = userId;
        this.category = category;
        this.limitAmount = limitAmount;
    }
}