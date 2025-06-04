package com.example.financeapp.ui.database;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.financeapp.ui.models.User;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao userDao;

    public UserRepository(AppDatabase db) {
        this.userDao = db.userDao();
    }

    public LiveData<User> login(String username, String password) {
        MutableLiveData<User> result = new MutableLiveData<>();
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = userDao.login(username, password);
            // Wrzuć wynik na główny wątek
            new Handler(Looper.getMainLooper()).post(() -> result.setValue(user));
        });
        return result;
    }

    public void insert(User user) {
        Executors.newSingleThreadExecutor().execute(() -> userDao.insert(user));
    }
}