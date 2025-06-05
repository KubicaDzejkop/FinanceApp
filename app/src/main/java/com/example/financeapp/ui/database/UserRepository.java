package com.example.financeapp.ui.database;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.financeapp.ui.models.User;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao userDao;

    public UserRepository(AppDatabase db) {
        this.userDao = db.userDao();
    }

    public void insert(User user) {
        Executors.newSingleThreadExecutor().execute(() -> userDao.insert(user));
    }

    public LiveData<User> getUserByUid(String uid) {
        MutableLiveData<User> result = new MutableLiveData<>();
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = userDao.getUserByUid(uid);
            result.postValue(user);
        });
        return result;
    }
}