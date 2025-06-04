package com.example.financeapp.ui.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.financeapp.ui.database.CategoryLimitRepository;
import com.example.financeapp.ui.models.CategoryLimit;
import java.util.List;

public class CategoryLimitViewModel extends ViewModel {
    private final CategoryLimitRepository repository;

    public CategoryLimitViewModel(CategoryLimitRepository repository) {
        this.repository = repository;
    }

    public void insertOrUpdate(CategoryLimit limit) {
        repository.insertOrUpdate(limit);
    }

    public LiveData<CategoryLimit> getLimit(String userId, String category) {
        return repository.getLimit(userId, category);
    }

    public LiveData<List<CategoryLimit>> getAllLimits(String userId) {
        return repository.getAllLimits(userId);
    }
}