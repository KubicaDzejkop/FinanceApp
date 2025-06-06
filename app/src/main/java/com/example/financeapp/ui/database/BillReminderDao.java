package com.example.financeapp.ui.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.financeapp.ui.models.BillReminder;

import java.util.List;

@Dao
public interface BillReminderDao {
    @Insert
    void insert(BillReminder reminder);

    @Update
    void update(BillReminder reminder);

    @Delete
    void delete(BillReminder reminder);

    @Query("SELECT * FROM bill_reminders WHERE userId = :userId ORDER BY paid ASC, dueDate ASC")
    LiveData<List<BillReminder>> getAll(String userId);

    @Query("SELECT * FROM bill_reminders WHERE userId = :userId ORDER BY paid ASC, dueDate ASC")
    List<BillReminder> getAllSync(String userId);

    @Query("SELECT * FROM bill_reminders WHERE id = :id")
    LiveData<BillReminder> getById(int id);

    @Query("SELECT * FROM bill_reminders WHERE id = :id")
    BillReminder getByIdSync(int id);

    @Query("SELECT * FROM bill_reminders WHERE userId = :userId AND paid = 0 ORDER BY dueDate ASC LIMIT 1")
    BillReminder getFirstUnpaid(String userId);

    @Query("SELECT COUNT(*) FROM bill_reminders WHERE userId = :userId AND paid = 0")
    int countUnpaid(String userId);

    @Query("SELECT * FROM bill_reminders WHERE userId = :userId AND paid = 0 ORDER BY dueDate ASC")
    List<BillReminder> getAllUnpaid(String userId);
}