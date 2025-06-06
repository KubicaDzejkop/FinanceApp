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

        // --- AUTOMATYCZNE GENEROWANIE PRZYPOMNIEŃ ZA RACHUNKI ---
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(requireContext());
            List<Transaction> allTransactions = db.transactionDao().getTransactionsForUser(userId);
            List<BillReminder> allReminders = db.billReminderDao().getAllSync(userId);

            java.time.LocalDate today = java.time.LocalDate.now();

            for (Transaction t : allTransactions) {
                if (t.getCategory() == null) continue;
                if (!t.getCategory().equalsIgnoreCase("rachunki")) continue;
                if (t.getDate() == null) continue;

                java.time.LocalDate transDate = java.time.LocalDate.parse(t.getDate());
                int paymentDay = transDate.getDayOfMonth();

                java.time.LocalDate nextBillDate = today.withDayOfMonth(Math.min(paymentDay, today.lengthOfMonth()));
                if (!nextBillDate.isAfter(today)) {
                    java.time.LocalDate nextMonth = today.plusMonths(1);
                    nextBillDate = nextMonth.withDayOfMonth(Math.min(paymentDay, nextMonth.lengthOfMonth()));
                }
                java.time.LocalDate reminderDate = nextBillDate.minusDays(2);

                // SPRAWDZENIE: czy już zapłacono rachunek za ten miesiąc i odbiorcę
                String billYearMonth = nextBillDate.toString().substring(0, 7);
                boolean alreadyPaid = false;
                for (Transaction t2 : allTransactions) {
                    if (t2.getCategory() != null
                            && t2.getCategory().equalsIgnoreCase("rachunki")
                            && t2.getRecipient().equalsIgnoreCase(t.getRecipient())
                            && t2.getDate().startsWith(billYearMonth)) {
                        alreadyPaid = true;
                        break;
                    }
                }

                // Dodaj przypomnienie jeśli dziś >= reminderDate i <= nextBillDate, NIE MA przypomnienia i NIE zapłacono
                boolean alreadyExists = false;
                if (allReminders != null) {
                    for (BillReminder br : allReminders) {
                        if (!br.paid && br.title.equalsIgnoreCase(t.getRecipient())
                                && br.dueDate.equals(nextBillDate.toString())) {
                            alreadyExists = true;
                            break;
                        }
                    }
                }
                if (!today.isBefore(reminderDate) && !today.isAfter(nextBillDate) && !alreadyExists && !alreadyPaid) {
                    BillReminder newReminder = new BillReminder();
                    newReminder.userId = userId;
                    newReminder.title = t.getRecipient();
                    newReminder.message = "Opłać rachunek za " + t.getRecipient() + " do " + nextBillDate.toString();
                    newReminder.dueDate = nextBillDate.toString();
                    newReminder.paid = false;
                    newReminder.notificationTime = java.time.ZonedDateTime.now().toInstant().toEpochMilli();
                    newReminder.amount = t.getAmount(); // Ustaw kwotę!
                    db.billReminderDao().insert(newReminder);
                }
            }
        }).start();

        BillReminderRepository repo = new BillReminderRepository(AppDatabase.getDatabase(requireContext()).billReminderDao());
        BillReminderViewModelFactory factory = new BillReminderViewModelFactory(repo);
        viewModel = new ViewModelProvider(this, factory).get(BillReminderViewModel.class);

        adapter.setOnPaidClickListener(reminder -> {
            // Po kliknięciu "Opłacone": usuń przypomnienie i dodaj transakcję za ten rachunek
            new Thread(() -> {
                AppDatabase db = AppDatabase.getDatabase(requireContext());
                db.billReminderDao().delete(reminder);

                // Dodaj transakcję za ten rachunek, aby nie pojawiało się ponownie
                Transaction paidBill = new Transaction(
                        reminder.userId,
                        reminder.title,
                        reminder.amount,
                        "rachunki",
                        reminder.dueDate,
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