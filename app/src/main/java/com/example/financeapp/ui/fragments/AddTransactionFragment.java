package com.example.financeapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.financeapp.R;
import com.example.financeapp.ui.ViewModels.TransactionViewModel;
import com.example.financeapp.ui.ViewModels.CategoryLimitViewModelFactory;
import com.example.financeapp.ui.ViewModels.CategoryLimitViewModel;
import com.example.financeapp.ui.ViewModels.TransactionViewModelFactory;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.TransactionRepository;
import com.example.financeapp.ui.models.Transaction;
import com.example.financeapp.ui.database.CategoryLimitRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransactionFragment extends Fragment {

    private EditText etAmount, etRecipient;
    private Spinner spCategory;
    private Button btnAuthorize;
    private TransactionViewModel transactionViewModel;
    private CategoryLimitViewModel limitViewModel;
    private String userId;

    private final String[] categories = new String[]{
            "Wybierz kategorię", "Rozrywka", "Rachunki", "Jedzenie", "Praca", "Transport", "Zdrowie", "Zakupy", "Inne", "Wynagrodzenie", "Zwrot", "Żywność"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CategoryLimitRepository limitRepository = new CategoryLimitRepository(AppDatabase.getDatabase(requireContext()).categoryLimitDao());
        CategoryLimitViewModelFactory limitFactory = new CategoryLimitViewModelFactory(limitRepository);
        limitViewModel = new ViewModelProvider(requireActivity(), limitFactory).get(CategoryLimitViewModel.class);

        etAmount = view.findViewById(R.id.etAmount);
        etRecipient = view.findViewById(R.id.etRecipient);
        spCategory = view.findViewById(R.id.spCategory);
        btnAuthorize = view.findViewById(R.id.btnAuthorize);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);
        spCategory.setSelection(0);

        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "-1");

        TransactionRepository repository = new TransactionRepository(AppDatabase.getDatabase(requireContext()).transactionDao());
        TransactionViewModelFactory factory = new TransactionViewModelFactory(repository);
        transactionViewModel = new ViewModelProvider(requireActivity(), factory).get(TransactionViewModel.class);

        btnAuthorize.setOnClickListener(v -> validateAndAddTransaction());

        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_home));
    }

    private void validateAndAddTransaction() {
        String amountStr = etAmount.getText().toString().trim();
        String recipient = etRecipient.getText().toString().trim();
        String category = (String) spCategory.getSelectedItem();


        if (TextUtils.isEmpty(amountStr) || TextUtils.isEmpty(recipient) || spCategory.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Wypełnij wszystkie pola!", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr.replace(",", "."));
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Nieprawidłowa kwota!", Toast.LENGTH_SHORT).show();
            return;
        }


        String type = "expense";
        if (category.equalsIgnoreCase("Wynagrodzenie") || category.equalsIgnoreCase("Zwrot") || category.equalsIgnoreCase("Praca")) {
            type = "income";
        }


        if (type.equals("expense") && amount > 0) amount = -amount;
        if (type.equals("income") && amount < 0) amount = -amount;

        String finalType = type;
        double finalAmount = amount;


        if (finalType.equals("expense")) {

            observeOnce(limitViewModel.getLimit(userId, category), getViewLifecycleOwner(), limit -> {

                if (limit == null) {
                    addTransaction(finalType, finalAmount, recipient, category);
                    return;
                }

                java.util.Calendar cal = java.util.Calendar.getInstance();
                String month = String.format(java.util.Locale.US, "%02d", cal.get(java.util.Calendar.MONTH) + 1);
                String year = String.valueOf(cal.get(java.util.Calendar.YEAR));
                observeOnce(transactionViewModel.getTotalExpensesByCategory(userId, category), getViewLifecycleOwner(), spent -> {
                    double alreadySpent = spent != null ? Math.abs(spent) : 0.0;
                    double amountAbs = Math.abs(finalAmount);
                    double newSpent = alreadySpent + amountAbs;
                    if (newSpent > limit.limitAmount || Math.abs(newSpent - limit.limitAmount) < 0.01) {
                        Toast.makeText(getContext(), "Przekroczony limit dla kategorii " + category.toUpperCase() + "!!", Toast.LENGTH_LONG).show();
                    } else {
                        addTransaction(finalType, finalAmount, recipient, category);
                    }
                });
            });
        } else {

            addTransaction(finalType, finalAmount, recipient, category);
        }
    }

    private void addTransaction(String type, double amount, String recipient, String category) {

        observeOnce(transactionViewModel.getBalance(userId), getViewLifecycleOwner(), currentBalance -> {
            if ("expense".equals(type) && currentBalance != null && (currentBalance + amount) < 0) {
                Toast.makeText(getContext(), "Za mało środków na koncie!", Toast.LENGTH_SHORT).show();
                return;
            }
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
            Transaction transaction = new Transaction(userId, recipient, amount, category, date, type);
            transactionViewModel.insertTransaction(transaction);

            Toast.makeText(getContext(), "Pomyślne dodanie transakcji", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).navigate(R.id.navigation_home);
        });
    }


    private static <T> void observeOnce(LiveData<T> liveData, LifecycleOwner owner, Observer<T> observer) {
        liveData.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(T t) {
                observer.onChanged(t);
                liveData.removeObserver(this);
            }
        });
    }
}