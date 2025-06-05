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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.TextView;
import android.widget.Toast;
import androidx.navigation.Navigation;

import com.example.financeapp.R;
import com.example.financeapp.ui.ViewModels.HistoryViewModel;
import com.example.financeapp.ui.ViewModels.HistoryViewModelFactory;
import com.example.financeapp.ui.adapters.TransactionsAdapter;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.TransactionRepository;
import com.example.financeapp.ui.models.Transaction;
import com.example.financeapp.ui.models.TransactionListItem;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private TransactionsAdapter adapter;
    private List<Transaction> allTransactions = new ArrayList<>();
    private String userId;
    private static final String[] CATEGORIES = {
            "Wybierz kategorię", "Rozrywka", "Rachunki", "Jedzenie", "Praca", "Transport",
            "Zdrowie", "Zakupy", "Inne", "Wynagrodzenie", "Zwrot", "Żywność"
    };

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

        userId = prefs.getString("user_id", "-1");
        if (userId == null || userId.equals("-1")) {
            Toast.makeText(requireContext(), "Błąd: użytkownik nie jest zalogowany!", Toast.LENGTH_SHORT).show();
            return view;
        }

        historyViewModel.getAllTransactions(userId).observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null) {
                allTransactions = transactions;
                adapter.setItems(buildSectionedList(transactions));
            }
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

        EditText searchEditText = view.findViewById(R.id.edit_search);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTransactions(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        Spinner spinnerCategory = view.findViewById(R.id.spinnerCategory);
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                CATEGORIES
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterSpinner);

        spinnerCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View v, int position, long id) {
                String selected = CATEGORIES[position];
                filterByCategory(selected);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        return view;
    }

    private void filterTransactions(String query) {
        String lowerQuery = query.trim().toLowerCase();
        if (lowerQuery.isEmpty()) {
            adapter.setItems(buildSectionedList(allTransactions));
            return;
        }
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : allTransactions) {
            if (t.getRecipient() != null && t.getRecipient().toLowerCase().contains(lowerQuery)) {
                filtered.add(t);
            }
        }
        adapter.setItems(buildSectionedList(filtered));
    }
    private void filterByCategory(String category) {
        if (category.equals("Wybierz kategorię")) {
            adapter.setItems(buildSectionedList(allTransactions));
            return;
        }
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : allTransactions) {
            if (category.equals(t.getCategory())) {
                filtered.add(t);
            }
        }
        adapter.setItems(buildSectionedList(filtered));
    }
    private List<TransactionListItem> buildSectionedList(List<Transaction> transactions) {
        List<TransactionListItem> result = new ArrayList<>();
        String lastDate = "";
        for (Transaction t : transactions) {
            if (!t.getDate().equals(lastDate)) {
                result.add(TransactionListItem.dateHeader(t.getDate()));
                lastDate = t.getDate();
            }
            result.add(TransactionListItem.transactionItem(t));
        }
        return result;
    }
}