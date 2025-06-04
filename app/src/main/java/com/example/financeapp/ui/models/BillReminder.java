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
    public String dueDate; // format: yyyy-MM-dd
    public boolean paid;

    @ColumnInfo(name = "notification_time")
    public long notificationTime;

    public BillReminder() {}
}