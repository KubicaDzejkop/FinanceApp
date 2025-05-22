package com.example.financeapp.ui.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.financeapp.ui.models.Transaction;
import com.example.financeapp.ui.database.TransactionDao;
import com.example.financeapp.ui.models.User;

@Database(entities = {User.class, Transaction.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public static AppDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "finance_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract TransactionDao transactionDao();
    public abstract UserDao userDao();
}