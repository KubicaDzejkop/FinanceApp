package com.example.financeapp.ui.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import com.example.financeapp.ui.models.Transaction;
import java.util.List;

@Dao
public interface    TransactionDao {
    @Insert
    void insert(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("DELETE FROM transactions WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("SELECT * FROM transactions WHERE type = 'income' ORDER BY date DESC")
    LiveData<List<Transaction>> getAllIncomes();

    @Query("SELECT * FROM transactions WHERE type = 'expense' ORDER BY date DESC")
    LiveData<List<Transaction>> getAllExpenses();

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'income'")
    LiveData<Double> getTotalIncomes();

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense'")
    LiveData<Double> getTotalExpenses();

    @Query("SELECT * FROM transactions WHERE category = :category")
    LiveData<List<Transaction>> getTransactionsByCategory(String category);

    @Query("SELECT * FROM transactions WHERE strftime('%m', date) = :month AND strftime('%Y', date) = :year")
    LiveData<List<Transaction>> getTransactionsByMonth(String month, String year);

    @Query("SELECT SUM(amount) FROM transactions")
    LiveData<Double> getBalance();
}