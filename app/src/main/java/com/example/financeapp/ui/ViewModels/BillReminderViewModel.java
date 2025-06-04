package com.example.financeapp.ui.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.financeapp.ui.database.BillReminderRepository;
import com.example.financeapp.ui.models.BillReminder;

import java.util.List;

public class BillReminderViewModel extends ViewModel {
    private final BillReminderRepository repository;

    public BillReminderViewModel(BillReminderRepository repo) { this.repository = repo; }

    public void insert(BillReminder b) { repository.insert(b);}
    public void update(BillReminder b) { repository.update(b);}
    public void delete(BillReminder b) { repository.delete(b);}
    public LiveData<List<BillReminder>> getAll(String userId) { return repository.getAll(userId);}
    public LiveData<BillReminder> getById(int id) { return repository.getById(id);}
}