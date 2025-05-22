package com.example.financeapp.ui.database;

import androidx.lifecycle.LiveData;
import com.example.financeapp.ui.models.User;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao userDao;

    public UserRepository(AppDatabase db) {
        this.userDao = db.userDao();
    }

    public LiveData<User> login(String username, String password) {
        return userDao.login(username, password);
    }

    public void insert(User user) {
        Executors.newSingleThreadExecutor().execute(() -> userDao.insert(user));
    }
}