package com.example.financeapp.ui.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.financeapp.ui.database.TransactionRepository;
import com.example.financeapp.ui.models.Transaction;

public class TransactionDetailsViewModel extends ViewModel {
    private TransactionRepository repository;

    public void setRepository(TransactionRepository repository) {
        this.repository = repository;
    }

    public LiveData<Transaction> getTransactionById(int transactionId, String userId) {
        return repository.getTransactionById(transactionId, userId);
    }
}