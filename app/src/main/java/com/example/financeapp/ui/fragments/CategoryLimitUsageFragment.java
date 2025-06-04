package com.example.financeapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financeapp.R;
import com.example.financeapp.ui.adapters.CategoryLimitUsageAdapter;
import com.example.financeapp.ui.ViewModels.CategoryLimitViewModel;
import com.example.financeapp.ui.ViewModels.CategoryLimitViewModelFactory;
import com.example.financeapp.ui.ViewModels.TransactionViewModel;
import com.example.financeapp.ui.ViewModels.TransactionViewModelFactory;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.CategoryLimitRepository;
import com.example.financeapp.ui.database.TransactionRepository;
import com.example.financeapp.ui.models.CategoryLimit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CategoryLimitUsageFragment extends Fragment {
    private RecyclerView rvCategoryLimits;
    private CategoryLimitUsageAdapter adapter;
    private CategoryLimitViewModel limitViewModel;
    private TransactionViewModel transactionViewModel;
    private String userId;
    private String[] categories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_limit_usage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCategoryLimits = view.findViewById(R.id.rvCategoryLimits);
        rvCategoryLimits.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoryLimitUsageAdapter(new ArrayList<>());
        rvCategoryLimits.setAdapter(adapter);

        categories = getResources().getStringArray(R.array.categories_array);

        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "-1");

        CategoryLimitRepository limitRepo = new CategoryLimitRepository(AppDatabase.getDatabase(requireContext()).categoryLimitDao());
        CategoryLimitViewModelFactory limitFactory = new CategoryLimitViewModelFactory(limitRepo);
        limitViewModel = new ViewModelProvider(requireActivity(), limitFactory).get(CategoryLimitViewModel.class);

        TransactionRepository transRepo = new TransactionRepository(AppDatabase.getDatabase(requireContext()).transactionDao());
        TransactionViewModelFactory transFactory = new TransactionViewModelFactory(transRepo);
        transactionViewModel = new ViewModelProvider(requireActivity(), transFactory).get(TransactionViewModel.class);

        limitViewModel.getAllLimits(userId).observe(getViewLifecycleOwner(), limits -> {
            Map<String, Double> categoryLimits = new HashMap<>();
            if (limits != null) {
                for (CategoryLimit limit : limits) {
                    categoryLimits.put(limit.category, limit.limitAmount);
                }
            }

            Calendar cal = Calendar.getInstance();
            String month = String.format(Locale.US, "%02d", cal.get(Calendar.MONTH) + 1);
            String year = String.valueOf(cal.get(Calendar.YEAR));

            int startIdx = 1;
            int categoryCount = categories.length - startIdx;

            List<CategoryLimitUsageAdapter.RowData> rows = new ArrayList<>();
            Map<String, Integer> categoryOrder = new HashMap<>();
            for (int i = startIdx; i < categories.length; i++) {
                categoryOrder.put(categories[i], i - startIdx); // 0,1,2...
            }


            final int[] loadedCount = {0};

            final CategoryLimitUsageAdapter.RowData[] tempRows = new CategoryLimitUsageAdapter.RowData[categoryCount];

            for (int i = startIdx; i < categories.length; i++) {
                final String category = categories[i];
                final double limit = categoryLimits.getOrDefault(category, 0.0);
                final int pos = i - startIdx;

                transactionViewModel.getTotalExpensesByCategory(userId, category)
                        .observe(getViewLifecycleOwner(), spent -> {
                            double used = spent != null ? Math.abs(spent) : 0.0;
                            double percent = (limit > 0) ? (used / limit) * 100.0 : 0.0;
                            tempRows[pos] = new CategoryLimitUsageAdapter.RowData(category, limit, percent);
                            loadedCount[0]++;


                            if (loadedCount[0] == categoryCount) {
                                rows.clear();
                                for (int j = 0; j < categoryCount; j++) {
                                    rows.add(tempRows[j]);
                                }
                                adapter.setData(new ArrayList<>(rows));
                            }
                        });
            }
        });
    }
}