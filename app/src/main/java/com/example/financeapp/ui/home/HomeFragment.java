package com.example.financeapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.financeapp.databinding.FragmentHomeBinding;
import com.example.financeapp.ui.dashboard.TransactionViewModel;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private TransactionViewModel viewModel;
    private RecentTransactionsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

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
        viewModel.getBalance().observe(getViewLifecycleOwner(), balance -> {
            binding.tvBalance.setText(String.format("%.2f PLN", balance));
        });

        viewModel.getRecentTransactions().observe(getViewLifecycleOwner(), transactions -> {
            adapter.submitList(transactions);
        });
    }

    private void setupClickListeners() {
        binding.btnAddTransaction.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_home_to_addTransactionFragment);
        });

        binding.btnShowMoreTransactions.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_home_to_historyFragment);
        });

        binding.btnShowMoreAnalysis.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_home_to_analysisFragment);
        });
    }
}