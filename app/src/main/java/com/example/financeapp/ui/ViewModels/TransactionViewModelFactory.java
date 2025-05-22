package com.example.financeapp.ui.ViewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.financeapp.ui.database.TransactionRepository;

public class TransactionViewModelFactory implements ViewModelProvider.Factory {
    private final TransactionRepository repository;

    public TransactionViewModelFactory(TransactionRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TransactionViewModel.class)) {
            return (T) new TransactionViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}