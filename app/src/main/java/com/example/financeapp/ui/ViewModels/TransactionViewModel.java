package com.example.financeapp.ui.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.financeapp.ui.database.TransactionRepository;
import com.example.financeapp.ui.models.Transaction;
import java.util.List;

public class TransactionViewModel extends ViewModel {
    private final TransactionRepository repository;
    private final LiveData<Double> balance;
    private final LiveData<List<Transaction>> transactions;

    public TransactionViewModel(TransactionRepository repository) {
        this.repository = repository;
        this.balance = repository.getBalance();
        this.transactions = repository.getAllTransactions();
    }

    // Metody dostępne dla wszystkich fragmentów
    public LiveData<Double> getBalance() {
        return balance;
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
    }

    public void insertTransaction(Transaction transaction) {
        repository.insert(transaction);
    }
}