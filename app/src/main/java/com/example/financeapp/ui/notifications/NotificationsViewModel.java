package com.example.financeapp.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {
    private final MutableLiveData<String> budgetSummary = new MutableLiveData<>();

    public NotificationsViewModel() {
        // Przykładowe dane (później zastąpisz danymi z Room Database)
        budgetSummary.setValue(
                "Budżet na październik:\n\n" +
                        "• Jedzenie: 600 zł (wykorzystano 80%)\n" +
                        "• Rozrywka: 200 zł (wykorzystano 50%)\n" +
                        "• Rachunki: 400 zł (opłacone)"
        );
    }

    public LiveData<String> getBudgetSummary() {
        return budgetSummary;
    }
}