package com.example.financeapp.ui.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.financeapp.ui.models.CategoryLimit;
import java.util.List;

@Dao
public interface CategoryLimitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(CategoryLimit limit);

    @Query("SELECT * FROM categorylimit WHERE userId = :userId AND category = :category LIMIT 1")
    LiveData<CategoryLimit> getLimit(String userId, String category);

    @Query("SELECT * FROM categorylimit WHERE userId = :userId")
    LiveData<List<CategoryLimit>> getAllLimits(String userId);
}