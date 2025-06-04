package com.example.financeapp.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.Navigation;

import com.example.financeapp.R;
import com.example.financeapp.ui.ViewModels.HistoryViewModel;
import com.example.financeapp.ui.ViewModels.HistoryViewModelFactory;
import com.example.financeapp.ui.adapters.TransactionsAdapter;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.BillReminderRepository;
import com.example.financeapp.ui.database.TransactionDao;
import com.example.financeapp.ui.database.TransactionRepository;
import com.example.financeapp.ui.models.BillReminder;
import com.example.financeapp.ui.models.Transaction;
import com.example.financeapp.ui.models.TransactionListItem;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private TransactionsAdapter adapter;
    private List<Transaction> allTransactions = new ArrayList<>();
    private String userId;
    private String selectedCategory = "Wybierz kategorię";
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

        String userId = prefs.getString("user_id", "-1");
        if (userId == null || userId.equals("-1")) {
            Toast.makeText(requireContext(), "Błąd: użytkownik nie jest zalogowany!", Toast.LENGTH_SHORT).show();
            return view;
        }

        insertSampleTransactionsIfEmpty(repository, userId);

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
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTransactions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
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
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = CATEGORIES[position];
                filterByCategory(selected);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });


        return view;
    }



    private void insertSampleTransactionsIfEmpty(TransactionRepository repository, String userId) {
        repository.getAllTransactions(userId).observe(getViewLifecycleOwner(), transactions -> {
            if (transactions == null || transactions.isEmpty()) {

                repository.insert(new Transaction(userId, "Biedronka", -54.99, "Zakupy", "2025-03-05", "expense"));
                repository.insert(new Transaction(userId, "Uber", -18.49, "Transport", "2025-03-10", "expense"));
                repository.insert(new Transaction(userId, "Firma XYZ", 2500.00, "Wynagrodzenie", "2025-03-27", "income"));
                repository.insert(new Transaction(userId, "Energia", -120.00, "Rachunki", "2025-03-15", "expense"));


                repository.insert(new Transaction(userId, "Lidl", -73.30, "Zakupy", "2025-04-01", "expense"));
                repository.insert(new Transaction(userId, "Netflix", -49.00, "Rozrywka", "2025-04-08", "expense"));
                repository.insert(new Transaction(userId, "Allegro", 120.00, "Zwrot", "2025-04-14", "income"));
                repository.insert(new Transaction(userId, "Pizzeria", -15.90, "Żywność", "2025-04-20", "expense"));


                repository.insert(new Transaction(userId, "PKP", -60.00, "Transport", "2025-05-12", "expense"));
                repository.insert(new Transaction(userId, "Internet", -200.00, "Rachunki", "2025-05-11", "expense"));
                repository.insert(new Transaction(userId, "KASIORKA", 3000.00, "Hustlerka", "2025-05-14", "income"));
                repository.insert(new Transaction(userId, "Apteka", -30.00, "Zdrowie", "2025-05-18", "expense"));


                repository.insert(new Transaction(userId, "Biedronka", -64.99, "Zakupy", "2025-02-05", "expense"));
                repository.insert(new Transaction(userId, "Uber", -11.49, "Transport", "2025-02-10", "expense"));
                repository.insert(new Transaction(userId, "Firma XYZ", 2600.00, "Wynagrodzenie", "2025-02-27", "income"));
                repository.insert(new Transaction(userId, "Energia", -100.00, "Rachunki", "2025-05-02", "expense"));


                repository.insert(new Transaction(userId, "Lidl", -123.30, "Zakupy", "2025-01-01", "expense"));
                repository.insert(new Transaction(userId, "Netflix", -55.00, "Rozrywka", "2025-01-08", "expense"));
                repository.insert(new Transaction(userId, "Allegro", 180.00, "Zwrot", "2025-01-14", "income"));
                repository.insert(new Transaction(userId, "Pizzeria", -20.90, "Żywność", "2025-01-20", "expense"));


                repository.insert(new Transaction(userId, "PKP", -50.00, "Transport", "2025-05-12", "expense"));
                repository.insert(new Transaction(userId, "Dentysta", -300.00, "Zdrowie", "2025-03-22", "expense"));
                repository.insert(new Transaction(userId, "Lotto", 200.00, "Wygrana", "2025-04-24", "income"));
                repository.insert(new Transaction(userId, "Mieszkanie", -1200.00, "Rachunki", "2025-01-01", "expense"));
            }
        });
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

    private void checkForRecurringBills() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<TransactionDao.RecurringPayment> recurring = AppDatabase.getDatabase(requireContext()).transactionDao().getRecurringBills(userId);
            if (!recurring.isEmpty()) {

                String recipient = recurring.get(0).recipient;
                requireActivity().runOnUiThread(() -> {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Wykryto powtarzalny rachunek")
                            .setMessage("Wykryto powtarzalny rachunek do: " + recipient + ". Chcesz dodać przypomnienie?")
                            .setPositiveButton("Tak", (dialog, which) -> {

                                BillReminder reminder = new BillReminder();
                                reminder.userId = userId;
                                reminder.title = "Rachunek - " + recipient;
                                reminder.message = "Zapłać rachunek do " + recipient + "!";
                                reminder.dueDate = getNextMonthDueDate(); // np. 2025-07-01
                                reminder.paid = false;
                                reminder.notificationTime = calculateNotificationTime(reminder.dueDate);

                                BillReminderRepository repo = new BillReminderRepository(AppDatabase.getDatabase(requireContext()).billReminderDao());
                                repo.insert(reminder);
                            })
                            .setNegativeButton("Nie", null)
                            .show();
                });
            }
        });
    }

    private String getNextMonthDueDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.getTime());
    }

    private long calculateNotificationTime(String dueDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(dueDate));
            cal.add(Calendar.DAY_OF_MONTH, -1);
            return cal.getTimeInMillis();
        } catch (Exception ignored) {}
        return 0;
    }
}
