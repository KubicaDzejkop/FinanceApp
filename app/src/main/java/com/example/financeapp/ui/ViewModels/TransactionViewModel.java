package com.example.financeapp.ui.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.TransactionRepository;
import com.example.financeapp.ui.models.MonthlyExpense;
import com.example.financeapp.ui.models.Transaction;
import java.util.List;

public class TransactionViewModel extends ViewModel {
    private final TransactionRepository repository;

    public TransactionViewModel(TransactionRepository repository) {
        this.repository = repository;
    }

    public LiveData<Double> getBalance(String userId) {
        return repository.getBalance(userId);
    }

    public LiveData<List<Transaction>> getTransactions(String userId) {
        return repository.getAllTransactions(userId);
    }

    public void insertTransaction(Transaction transaction) {
        repository.insert(transaction);
    }
    public LiveData<Double> getMonthlyExpenses(String userId, String month, String year) {
        return repository.getMonthlyExpenses(userId, month, year);
    }

    public LiveData<List<MonthlyExpense>> getLast3MonthsExpenses(String userId, String year, String prevYear) {
        return repository.getLast3MonthsExpenses(userId, year, prevYear);
    }
    public LiveData<Double> getTotalExpensesByCategory(String userId, String category) {
        return repository.getTotalExpensesByCategory(userId, category);
    }
}