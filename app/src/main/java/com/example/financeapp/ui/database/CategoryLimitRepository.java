package com.example.financeapp.ui.database;

import androidx.lifecycle.LiveData;
import com.example.financeapp.ui.models.CategoryLimit;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryLimitRepository {
    private final CategoryLimitDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public CategoryLimitRepository(CategoryLimitDao dao) {
        this.dao = dao;
    }

    public void insertOrUpdate(CategoryLimit limit) {
        executor.execute(() -> dao.insertOrUpdate(limit));
    }

    public LiveData<CategoryLimit> getLimit(String userId, String category) {
        return dao.getLimit(userId, category);
    }

    public LiveData<List<CategoryLimit>> getAllLimits(String userId) {
        return dao.getAllLimits(userId);
    }
}