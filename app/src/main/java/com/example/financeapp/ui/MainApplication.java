package com.example.financeapp.ui;

import android.app.Application;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.TransactionRepository;

public class MainApplication extends Application {
    private TransactionRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();

        // Teraz getDatabase() jest dostępne
        AppDatabase database = AppDatabase.getDatabase(this);
        repository = new TransactionRepository(database.transactionDao());
    }

    public TransactionRepository getRepository() {
        return repository;
    }
}