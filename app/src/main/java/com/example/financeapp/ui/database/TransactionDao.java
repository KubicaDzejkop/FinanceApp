package com.example.financeapp.ui.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;
import com.example.financeapp.ui.models.MonthlyExpense;
import com.example.financeapp.ui.models.Transaction;
import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insert(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Query("DELETE FROM transactions WHERE id = :id AND userId = :userId")
    void deleteById(int id, String userId);

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC, id DESC")
    LiveData<List<Transaction>> getAllTransactions(String userId);

    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = 'income' ORDER BY date DESC")
    LiveData<List<Transaction>> getAllIncomes(String userId);

    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = 'expense' ORDER BY date DESC")
    LiveData<List<Transaction>> getAllExpenses(String userId);

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = 'income'")
    LiveData<Double> getTotalIncomes(String userId);

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = 'expense'")
    LiveData<Double> getTotalExpenses(String userId);

    @Query("SELECT * FROM transactions WHERE userId = :userId AND category = :category")
    LiveData<List<Transaction>> getTransactionsByCategory(String userId, String category);

    @Query("SELECT * FROM transactions WHERE userId = :userId AND strftime('%m', date) = :month AND strftime('%Y', date) = :year")
    LiveData<List<Transaction>> getTransactionsByMonth(String userId, String month, String year);

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId")
    LiveData<Double> getBalance(String userId);

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC LIMIT 3")
    LiveData<List<Transaction>> getRecentTransactions(String userId);

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = 'expense' AND strftime('%m', date) = :month AND strftime('%Y', date) = :year")
    LiveData<Double> getMonthlyExpenses(String userId, String month, String year);

    @Query("SELECT strftime('%m', date) AS month, SUM(amount) as total FROM transactions WHERE userId = :userId AND type = 'expense' AND (strftime('%Y', date) = :year OR strftime('%Y', date) = :prevYear) GROUP BY strftime('%Y-%m', date) ORDER BY strftime('%Y-%m', date) DESC LIMIT 3")
    LiveData<List<MonthlyExpense>> getLast3MonthsExpenses(String userId, String year, String prevYear);

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = 'expense' AND category = :category")
    LiveData<Double> getTotalExpensesByCategory(String userId, String category);

    @Query("SELECT recipient, COUNT(*) as cnt FROM transactions WHERE userId = :userId AND category = 'Rachunki' GROUP BY recipient HAVING cnt >= 2")
    List<RecurringPayment> getRecurringBills(String userId);

    @Query("SELECT * FROM transactions WHERE userId = :userId AND category = :category AND paid = 1 AND date >= :fromDate AND date <= :toDate")
    LiveData<List<Transaction>> getPaidBillsLastMonth(String userId, String category, String fromDate, String toDate);

    @Query("SELECT * FROM transactions WHERE id = :transactionId AND userId = :userId LIMIT 1")
    LiveData<Transaction> getTransactionById(int transactionId, String userId);

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    List<Transaction> getTransactionsForUser(String userId);

    public static class RecurringPayment {
        public String recipient;
        public int cnt;
    }
}