package com.example.financeapp.ui.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> profileSummary = new MutableLiveData<>("Twoje dane profilowe");

    public LiveData<String> getProfileSummary() {
        return profileSummary;
    }

    // Możesz dodać więcej logiki, np. pobieranie danych z bazy itd.
}