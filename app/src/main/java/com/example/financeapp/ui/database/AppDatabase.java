package com.example.financeapp.ui.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.financeapp.ui.models.BillReminder;
import com.example.financeapp.ui.models.Transaction;
import com.example.financeapp.ui.models.User;
import com.example.financeapp.ui.models.CategoryLimit; // <-- dodaj import

@Database(entities = {User.class, Transaction.class, CategoryLimit.class, BillReminder.class}, version = 11) // <-- dodaj CategoryLimit, zwiększ wersję
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getDatabase(final Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "financeapp_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract TransactionDao transactionDao();
    public abstract UserDao userDao();
    public abstract CategoryLimitDao categoryLimitDao();
    public abstract BillReminderDao billReminderDao();// <-- dodaj nowy DAO
}