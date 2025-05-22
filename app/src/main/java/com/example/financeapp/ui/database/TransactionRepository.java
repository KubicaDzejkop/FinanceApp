package com.example.financeapp.ui.database;

import androidx.lifecycle.LiveData;
import com.example.financeapp.ui.models.Transaction;
import java.util.List;

public class TransactionRepository {
    private final TransactionDao transactionDao;

    public TransactionRepository(TransactionDao dao) {
        this.transactionDao = dao;
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return transactionDao.getAllTransactions();
    }

    public void insert(Transaction transaction) {
        new Thread(() -> transactionDao.insert(transaction)).start();
    }

    public void delete(int id) {
        new Thread(() -> transactionDao.delete(id)).start();
    }

    public LiveData<Double> getBalance() {
        return transactionDao.getBalance();
    }

    public LiveData<List<Transaction>> getMonthlyTransactions(int month, int year) {
        return transactionDao.getTransactionsByMonth(month, year);
    }
}