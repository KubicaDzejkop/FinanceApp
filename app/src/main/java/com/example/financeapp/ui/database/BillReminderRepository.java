package com.example.financeapp.ui.database;

import androidx.lifecycle.LiveData;

import com.example.financeapp.ui.models.BillReminder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BillReminderRepository {
    private final BillReminderDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public BillReminderRepository(BillReminderDao dao) { this.dao = dao; }

    public void insert(BillReminder reminder) { executor.execute(() -> dao.insert(reminder)); }
    public void update(BillReminder reminder) { executor.execute(() -> dao.update(reminder)); }
    public void delete(BillReminder reminder) { executor.execute(() -> dao.delete(reminder)); }
    public LiveData<List<BillReminder>> getAll(String userId) { return dao.getAll(userId); }
    public LiveData<BillReminder> getById(int id) { return dao.getById(id); }
}