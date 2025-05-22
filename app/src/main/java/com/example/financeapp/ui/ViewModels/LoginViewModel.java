package com.example.financeapp.ui.ViewModels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.UserRepository;
import com.example.financeapp.ui.models.User;

public class LoginViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    public final MutableLiveData<User> loggedUser = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(AppDatabase.getDatabase(application));
    }

    public LiveData<User> login(String username, String password) {
        return userRepository.login(username, password);
    }
}