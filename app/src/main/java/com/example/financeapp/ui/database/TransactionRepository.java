package com.example.financeapp.ui.database;

import androidx.lifecycle.LiveData;
import com.example.financeapp.ui.models.Transaction;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionRepository {
    private final TransactionDao transactionDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public TransactionRepository(TransactionDao dao) {
        this.transactionDao = dao;
    }

    public LiveData<List<Transaction>> getAllTransactions(String userId) {
        return transactionDao.getAllTransactions(userId);
    }

    public LiveData<List<Transaction>> getAllIncomes() {
        return transactionDao.getAllIncomes();
    }

    public LiveData<List<Transaction>> getAllExpenses() {
        return transactionDao.getAllExpenses();
    }

    public LiveData<Double> getTotalIncomes() {
        return transactionDao.getTotalIncomes();
    }

    public LiveData<Double> getTotalExpenses() {
        return transactionDao.getTotalExpenses();
    }

    public LiveData<List<Transaction>> getTransactionsByCategory(String category) {
        return transactionDao.getTransactionsByCategory(category);
    }

    public LiveData<List<Transaction>> getTransactionsByMonth(String month, String year) {
        return transactionDao.getTransactionsByMonth(month, year);
    }

    public LiveData<Double> getBalance() {
        return transactionDao.getBalance();
    }

    public void insert(Transaction transaction) {
        executorService.execute(() -> transactionDao.insert(transaction));
    }

    public void delete(Transaction transaction) {
        executorService.execute(() -> transactionDao.delete(transaction));
    }

    public void deleteById(int id) {
        executorService.execute(() -> transactionDao.deleteById(id));
    }

    public LiveData<Transaction> getTransactionById(int transactionId, String userId) {
        return transactionDao.getTransactionById(transactionId, userId);
    }
}