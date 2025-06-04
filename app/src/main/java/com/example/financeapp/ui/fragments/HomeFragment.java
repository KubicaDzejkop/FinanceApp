package com.example.financeapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.financeapp.R;
import com.example.financeapp.databinding.FragmentHomeBinding;
import com.example.financeapp.ui.ViewModels.TransactionViewModel;
import com.example.financeapp.ui.ViewModels.TransactionViewModelFactory;
import com.example.financeapp.ui.adapters.RecentTransactionsAdapter;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.TransactionRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.financeapp.ui.models.MonthlyExpense;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private TransactionViewModel viewModel;
    private RecentTransactionsAdapter adapter;

    private String userId = String.valueOf(-1);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        TransactionRepository repository = new TransactionRepository(db.transactionDao());
        TransactionViewModelFactory factory = new TransactionViewModelFactory(repository);
        viewModel = new ViewModelProvider(requireActivity(), factory).get(TransactionViewModel.class);


        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", String.valueOf(-1));

        setupRecyclerView();
        setupObservers();
        setupClickListeners();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new RecentTransactionsAdapter();
        binding.rvRecentTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvRecentTransactions.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getBalance(userId).observe(getViewLifecycleOwner(), balance -> {
            binding.tvBalance.setText(String.format("%.2f PLN", balance != null ? balance : 0.0));
        });

        viewModel.getTransactions(userId).observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null && transactions.size() > 5) {
                adapter.submitList(transactions.subList(0, 5));
            } else {
                adapter.submitList(transactions);
            }
        });


        Calendar cal = Calendar.getInstance();
        String month = String.format(Locale.US, "%02d", cal.get(Calendar.MONTH) + 1); // 01-12
        String year = String.valueOf(cal.get(Calendar.YEAR));
        viewModel.getMonthlyExpenses(userId, month, year).observe(getViewLifecycleOwner(), sum -> {
            double value = sum != null ? Math.abs(sum) : 0.0;
            binding.tvMonthlySpending.setText(String.format("%.2f PLN", value));
        });


        String prevYear = String.valueOf(cal.get(Calendar.YEAR) - 1);
        String[] monthLabels = {"mar", "kwi", "maj"};
        String[] monthNumbers = {"03", "04", "05"};

        viewModel.getLast3MonthsExpenses(userId, year, prevYear).observe(getViewLifecycleOwner(), list -> {

            Map<String, Double> expensesMap = new HashMap<>();
            for (int i = 0; i < monthNumbers.length; i++) {
                expensesMap.put(monthNumbers[i], 0.0);
            }
            if (list != null) {
                for (MonthlyExpense m : list) {
                    if (expensesMap.containsKey(m.month)) {
                        expensesMap.put(m.month, Math.abs(m.total));
                    }
                }
            }

            double max = 0;
            for (double val : expensesMap.values()) if (val > max) max = val;


            int[] barIds = {R.id.barMarch, R.id.barApril, R.id.barMay};
            for (int i = 0; i < monthNumbers.length; i++) {
                double amount = expensesMap.get(monthNumbers[i]);
                int barHeight = max > 0 ? (int)(60 * amount / max) + 10 : 10;
                View bar = binding.getRoot().findViewById(barIds[i]);
                ViewGroup.LayoutParams params = bar.getLayoutParams();
                params.height = barHeight;
                bar.setLayoutParams(params);

            }

        });
    }

    private void setupClickListeners() {
        binding.btnAddTransaction.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_home_to_addTransactionFragment);
        });

        binding.btnShowMoreTransactions.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_home_to_historyFragment);

            BottomNavigationView navView = requireActivity().findViewById(R.id.bottomNavigationView);
            navView.setSelectedItemId(R.id.navigation_history);
        });

        binding.btnShowMoreAnalysis.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("source", "home");
            Navigation.findNavController(v).navigate(R.id.navigation_analysis, bundle);
        });
    }
}
