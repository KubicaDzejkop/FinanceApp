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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.financeapp.R;
import com.example.financeapp.ui.ViewModels.CategoryLimitViewModel;
import com.example.financeapp.ui.ViewModels.CategoryLimitViewModelFactory;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.CategoryLimitRepository;
import com.example.financeapp.ui.models.CategoryLimit;

public class SetCategoryLimitFragment extends Fragment {
    private Spinner spCategory;
    private EditText etLimitAmount;
    private CategoryLimitViewModel limitViewModel;
    private String userId;

    private final String[] categories = new String[]{
            "Rozrywka", "Rachunki", "Jedzenie", "Praca", "Transport", "Zdrowie", "Zakupy", "Inne", "Wynagrodzenie", "Zwrot", "Żywność"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set_category_limit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnBack = view.findViewById(R.id.btn_back_set_limit);
        btnBack.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        spCategory = view.findViewById(R.id.spCategory);
        etLimitAmount = view.findViewById(R.id.etLimitAmount);
        Button btnSave = view.findViewById(R.id.btnSaveLimit);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "-1");

        CategoryLimitRepository repository = new CategoryLimitRepository(AppDatabase.getDatabase(requireContext()).categoryLimitDao());
        CategoryLimitViewModelFactory factory = new CategoryLimitViewModelFactory(repository);
        limitViewModel = new ViewModelProvider(requireActivity(), factory).get(CategoryLimitViewModel.class);


        btnSave.setOnClickListener(v -> {
            String category = (String) spCategory.getSelectedItem();
            String amountStr = etLimitAmount.getText().toString().trim();
            if (TextUtils.isEmpty(amountStr)) {
                Toast.makeText(getContext(), "Podaj limit!", Toast.LENGTH_SHORT).show();
                return;
            }
            double amount = Double.parseDouble(amountStr.replace(",", "."));
            CategoryLimit limit = new CategoryLimit();
            limit.userId = userId;
            limit.category = category;
            limit.limitAmount = amount;
            limitViewModel.insertOrUpdate(limit);
            Toast.makeText(getContext(), "Limit zapisany!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).popBackStack();
        });
    }
}