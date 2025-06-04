package com.example.financeapp.ui.ViewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.financeapp.ui.database.CategoryLimitRepository;

public class CategoryLimitViewModelFactory implements ViewModelProvider.Factory {
    private final CategoryLimitRepository repository;

    public CategoryLimitViewModelFactory(CategoryLimitRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CategoryLimitViewModel.class)) {
            return (T) new CategoryLimitViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}