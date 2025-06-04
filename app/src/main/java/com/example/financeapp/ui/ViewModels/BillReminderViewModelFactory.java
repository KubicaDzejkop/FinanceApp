package com.example.financeapp.ui.ViewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.financeapp.ui.database.BillReminderRepository;

public class BillReminderViewModelFactory implements ViewModelProvider.Factory {
    private final BillReminderRepository repository;

    public BillReminderViewModelFactory(BillReminderRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BillReminderViewModel.class)) {
            return (T) new BillReminderViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}