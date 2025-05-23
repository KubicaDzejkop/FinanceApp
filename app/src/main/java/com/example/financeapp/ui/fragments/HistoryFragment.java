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
import android.widget.Toast;

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

        String userId = prefs.getString("user_id", "-1");
        if (userId == null || userId.equals("-1")) {
            Toast.makeText(requireContext(), "Błąd: użytkownik nie jest zalogowany!", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Dodaj przykładowe transakcje, jeśli historia jest pusta
        insertSampleTransactionsIfEmpty(repository, userId);

        // Obserwuj transakcje i podpinaj do adaptera
        historyViewModel.getAllTransactions(userId).observe(getViewLifecycleOwner(), transactions -> {
            adapter.setTransactions(transactions);
            // Debug: możesz usunąć ten toast
            // Toast.makeText(requireContext(), "Transakcji: " + (transactions != null ? transactions.size() : 0), Toast.LENGTH_SHORT).show();
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

    // Dodaje przykładowe transakcje tylko jeśli lista jest pusta
    private void insertSampleTransactionsIfEmpty(TransactionRepository repository, String userId) {
        repository.getAllTransactions(userId).observe(getViewLifecycleOwner(), transactions -> {
            if (transactions == null || transactions.isEmpty()) {
                repository.insert(new com.example.financeapp.ui.models.Transaction(userId, "Biedronka", -54.99, "Zakupy", "2025-05-20", "expense"));
                repository.insert(new com.example.financeapp.ui.models.Transaction(userId, "Uber", -18.49, "Transport", "2025-05-19", "expense"));
                repository.insert(new com.example.financeapp.ui.models.Transaction(userId, "Firma XYZ", 2500.00, "Wynagrodzenie", "2025-05-18", "income"));
                repository.insert(new com.example.financeapp.ui.models.Transaction(userId, "Energia", -120.00, "Rachunki", "2025-05-17", "expense"));
                repository.insert(new com.example.financeapp.ui.models.Transaction(userId, "Lidl", -73.30, "Zakupy", "2025-05-16", "expense"));
                repository.insert(new com.example.financeapp.ui.models.Transaction(userId, "Netflix", -49.00, "Rozrywka", "2025-05-15", "expense"));
                repository.insert(new com.example.financeapp.ui.models.Transaction(userId, "Allegro", 120.00, "Zwrot", "2025-05-14", "income"));
                repository.insert(new com.example.financeapp.ui.models.Transaction(userId, "Pizzeria", -15.90, "Żywność", "2025-05-13", "expense"));
                repository.insert(new com.example.financeapp.ui.models.Transaction(userId, "PKP", -60.00, "Transport", "2025-05-12", "expense"));
                repository.insert(new com.example.financeapp.ui.models.Transaction(userId, "Internet", -200.00, "Rachunki", "2025-05-11", "expense"));
            }
        });
    }
}