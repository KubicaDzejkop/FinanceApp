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
import android.widget.ImageView;
import android.widget.Toast;
import androidx.navigation.Navigation;
import androidx.navigation.NavController;

import com.example.financeapp.R;
import com.example.financeapp.ui.adapters.BillReminderAdapter;
import com.example.financeapp.ui.ViewModels.BillReminderViewModel;
import com.example.financeapp.ui.ViewModels.BillReminderViewModelFactory;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.BillReminderRepository;
import com.example.financeapp.ui.models.Transaction;
import com.example.financeapp.ui.models.BillReminder;

import java.time.LocalDate;
import java.util.List;

public class BillReminderListFragment extends Fragment {
    private BillReminderViewModel viewModel;
    private BillReminderAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_reminder_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_bill_reminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BillReminderAdapter();
        recyclerView.setAdapter(adapter);

        ImageView btnBack = view.findViewById(R.id.btn_back_bill_reminder);
        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.navigation_profile);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", "-1");

        BillReminderRepository repo = new BillReminderRepository(AppDatabase.getDatabase(requireContext()).billReminderDao());
        BillReminderViewModelFactory factory = new BillReminderViewModelFactory(repo);
        viewModel = new ViewModelProvider(this, factory).get(BillReminderViewModel.class);

        adapter.setOnPaidClickListener(reminder -> {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getDatabase(requireContext());

                double saldo = 0.0;
                List<Transaction> transactions = db.transactionDao().getTransactionsForUser(reminder.userId);
                for (Transaction t : transactions) {
                    saldo += t.getAmount();
                }

                double kwotaDoZaplaty = Math.abs(reminder.amount);
                if (saldo < kwotaDoZaplaty) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Brak środków, aby opłacić rachunek!", Toast.LENGTH_LONG).show()
                    );
                    return;
                }

                db.billReminderDao().delete(reminder);

                String today = LocalDate.now().toString();

                Transaction paidBill = new Transaction(
                        reminder.userId,
                        reminder.title,
                        -kwotaDoZaplaty,
                        "Rachunki",
                        today,
                        "expense"
                );
                db.transactionDao().insert(paidBill);

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Przypomnienie usunięte i oznaczone jako opłacone", Toast.LENGTH_SHORT).show()
                );
            }).start();
        });

        adapter.setOnDeleteClickListener(reminder -> {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getDatabase(requireContext());
                db.billReminderDao().delete(reminder);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Przypomnienie usunięte", Toast.LENGTH_SHORT).show()
                );
            }).start();
        });

        viewModel.getAll(userId).observe(getViewLifecycleOwner(), reminders -> {
            adapter.setData(reminders);
        });
    }
}