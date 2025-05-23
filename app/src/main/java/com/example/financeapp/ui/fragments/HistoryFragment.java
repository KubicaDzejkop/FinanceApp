package com.example.financeapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.Navigation;

import com.example.financeapp.R;
import com.example.financeapp.ui.ViewModels.HistoryViewModel;
import com.example.financeapp.ui.ViewModels.HistoryViewModelFactory;
import com.example.financeapp.ui.adapters.TransactionsAdapter;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.TransactionRepository;

import android.widget.TextView;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private TransactionsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        TransactionRepository repository = new TransactionRepository(AppDatabase.getDatabase(requireContext()).transactionDao());
        HistoryViewModelFactory factory = new HistoryViewModelFactory(repository);
        historyViewModel = new ViewModelProvider(this, factory).get(HistoryViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionsAdapter();
        recyclerView.setAdapter(adapter);

        historyViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(HistoryViewModel.class);

        // Przekaż userId zalogowanego użytkownika!
        int userId = prefs.getInt("user_id", -1);
        historyViewModel.getAllTransactions(userId).observe(getViewLifecycleOwner(), transactions -> {
            adapter.setTransactions(transactions);
        });

        adapter.setOnTransactionClickListener(transaction -> {
            Bundle bundle = new Bundle();
            bundle.putInt("transactionId", transaction.getId());
            Navigation.findNavController(view).navigate(R.id.action_historyFragment_to_transactionDetailsFragment, bundle);
        });

        TextView analysisBtn = view.findViewById(R.id.text_analysis);
        analysisBtn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("source", "history");
            Navigation.findNavController(view).navigate(R.id.navigation_analysis, bundle);
        });

        return view;
    }
}