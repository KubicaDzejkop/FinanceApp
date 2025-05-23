package com.example.financeapp.ui.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.financeapp.ui.database.TransactionRepository;
import com.example.financeapp.ui.models.Transaction;
import java.util.List;

public class HistoryViewModel extends ViewModel {
    private TransactionRepository repository;

    public HistoryViewModel(TransactionRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<Transaction>> getAllTransactions(String userId) {
        return repository.getAllTransactions(userId);
    }
}