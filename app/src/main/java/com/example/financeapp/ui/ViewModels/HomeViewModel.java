package com.example.financeapp.ui.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> totalSpent = new MutableLiveData<>();

    public HomeViewModel() {
        totalSpent.setValue("1200.50");
    }

    public LiveData<String> getTotalSpent() {
        return totalSpent;
    }
}