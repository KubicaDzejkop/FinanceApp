package com.example.financeapp.ui.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.lifecycle.LiveData;
import com.example.financeapp.ui.models.Transaction;
import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insert(Transaction transaction);

    @Query("DELETE FROM transactions WHERE id = :id")
    void delete(int id);

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense'")
    LiveData<Double> getTotalExpenses();

    @Query("SELECT * FROM transactions WHERE category = :category")
    LiveData<List<Transaction>> getTransactionsByCategory(String category);
}