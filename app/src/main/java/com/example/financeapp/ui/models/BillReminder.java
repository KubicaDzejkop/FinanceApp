package com.example.financeapp.ui.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "bill_reminders")
public class BillReminder {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String userId;
    public String title;
    public String message;
    public String dueDate;
    public boolean paid;

    @ColumnInfo(name = "notification_time")
    public long notificationTime;


    public double amount;

    public BillReminder() {}

    public BillReminder(String userId, String title, String message, String dueDate, boolean paid, long notificationTime, double amount) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.dueDate = dueDate;
        this.paid = paid;
        this.notificationTime = notificationTime;
        this.amount = amount;
    }
}