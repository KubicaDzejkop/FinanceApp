package com.example.financeapp.ui.fragments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financeapp.MainActivity;
import com.example.financeapp.R;
import com.example.financeapp.ui.adapters.TransactionsAdapter;
import com.example.financeapp.ui.models.Transaction;
import com.example.financeapp.ui.models.TransactionListItem;
import com.example.financeapp.ui.models.BillReminder;
import com.example.financeapp.ui.database.AppDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private TextView tvBalance, tvMonthlySpending;
    private RecyclerView rvRecentTransactions;
    private ImageView btnAddTransaction;
    private TextView btnShowMoreTransactions, btnShowMoreAnalysis;

    // Słupki wykresu i kwoty
    private View barMarchIn, barMarchOut, barAprilIn, barAprilOut, barMayIn, barMayOut;
    private TextView labelMarch, labelApril, labelMay;
    private TextView valueMarchIn, valueMarchOut, valueAprilIn, valueAprilOut, valueMayIn, valueMayOut;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvBalance = view.findViewById(R.id.tvBalance);
        tvMonthlySpending = view.findViewById(R.id.tvMonthlySpending);
        rvRecentTransactions = view.findViewById(R.id.rvRecentTransactions);
        btnAddTransaction = view.findViewById(R.id.btnAddTransaction);
        btnShowMoreTransactions = view.findViewById(R.id.btnShowMoreTransactions);
        btnShowMoreAnalysis = view.findViewById(R.id.btnShowMoreAnalysis);

        // Słupki i kwoty (IN/OUT osobno!)
        barMarchIn = view.findViewById(R.id.barMarchIn);
        barMarchOut = view.findViewById(R.id.barMarchOut);
        barAprilIn = view.findViewById(R.id.barAprilIn);
        barAprilOut = view.findViewById(R.id.barAprilOut);
        barMayIn = view.findViewById(R.id.barMayIn);
        barMayOut = view.findViewById(R.id.barMayOut);

        labelMarch = view.findViewById(R.id.labelMarch);
        labelApril = view.findViewById(R.id.labelApril);
        labelMay = view.findViewById(R.id.labelMay);

        valueMarchIn = view.findViewById(R.id.valueMarchIn);
        valueMarchOut = view.findViewById(R.id.valueMarchOut);
        valueAprilIn = view.findViewById(R.id.valueAprilIn);
        valueAprilOut = view.findViewById(R.id.valueAprilOut);
        valueMayIn = view.findViewById(R.id.valueMayIn);
        valueMayOut = view.findViewById(R.id.valueMayOut);

        CardView cardAnalysis = view.findViewById(R.id.cardAnalysis);
        CardView cardTransactions = view.findViewById(R.id.cardTransactions);
        int lightViolet = ContextCompat.getColor(requireContext(), R.color.light_violet);
        if(cardAnalysis != null) cardAnalysis.setCardBackgroundColor(lightViolet);
        if(cardTransactions != null) cardTransactions.setCardBackgroundColor(lightViolet);

        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAddTransaction.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.addTransactionFragment)
        );
        btnShowMoreTransactions.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_historyFragment)
        );
        btnShowMoreAnalysis.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_analysisFragment)
        );

        btnShowMoreTransactions.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        btnShowMoreAnalysis.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        btnShowMoreTransactions.setTypeface(null, Typeface.BOLD);
        btnShowMoreAnalysis.setTypeface(null, Typeface.BOLD);

        // Najpierw generuj przypomnienia, potem wysyłaj PUSH jeśli to zimny start/logowanie
        generateBillRemindersAndShowPush();

        loadHomeData();
        return view;
    }

    private void generateBillRemindersAndShowPush() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        if (userId == null) return;

        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(requireContext());
            List<Transaction> transactions = db.transactionDao().getTransactionsForUser(userId);
            List<BillReminder> reminders = db.billReminderDao().getAllSync(userId);

            // Generowanie przypomnień
            Map<String, Transaction> latestBills = new HashMap<>();
            for (Transaction t : transactions) {
                if (t.getCategory() != null && t.getCategory().equalsIgnoreCase("rachunki")) {
                    String recipient = t.getRecipient();
                    if (!latestBills.containsKey(recipient) || t.getDate().compareTo(latestBills.get(recipient).getDate()) > 0) {
                        latestBills.put(recipient, t);
                    }
                }
            }

            java.time.LocalDate today = java.time.LocalDate.now();

            for (Transaction t : latestBills.values()) {
                java.time.LocalDate transDate = java.time.LocalDate.parse(t.getDate());
                int paymentDay = transDate.getDayOfMonth();

                java.time.LocalDate billDate = today.withDayOfMonth(Math.min(paymentDay, today.lengthOfMonth()));
                if (!billDate.isAfter(today)) {
                    java.time.LocalDate nextMonth = today.plusMonths(1);
                    billDate = nextMonth.withDayOfMonth(Math.min(paymentDay, nextMonth.lengthOfMonth()));
                }
                java.time.LocalDate reminderDate = billDate.minusDays(2);

                String billYearMonth = billDate.toString().substring(0, 7);
                boolean alreadyPaid = false;
                for (Transaction t2 : transactions) {
                    if (t2.getCategory() != null
                            && t2.getCategory().equalsIgnoreCase("rachunki")
                            && t2.getRecipient().equalsIgnoreCase(t.getRecipient())
                            && t2.getDate().startsWith(billYearMonth)) {
                        alreadyPaid = true;
                        break;
                    }
                }

                boolean alreadyExists = false;
                for (BillReminder br : reminders) {
                    if (br.title.equalsIgnoreCase(t.getRecipient())
                            && br.dueDate.equals(billDate.toString())) {
                        alreadyExists = true;
                        break;
                    }
                }
                if (!today.isBefore(reminderDate) && !alreadyExists && !alreadyPaid) {
                    BillReminder newReminder = new BillReminder();
                    newReminder.userId = userId;
                    newReminder.title = t.getRecipient();
                    newReminder.message = "Opłać rachunek za " + t.getRecipient() + " do " + billDate.toString();
                    newReminder.dueDate = billDate.toString();
                    newReminder.paid = false;
                    newReminder.notificationTime = java.time.ZonedDateTime.now().toInstant().toEpochMilli();
                    newReminder.amount = t.getAmount();
                    db.billReminderDao().insert(newReminder);
                }
            }

            // Teraz sprawdź i wyślij push TYLKO jeśli bill_reminder_notified == false (czyli po logowaniu/zimnym starcie)
            boolean notified = prefs.getBoolean("bill_reminder_notified", false);
            if (!notified) {
                BillReminder reminder = db.billReminderDao().getFirstUnpaid(userId);
                if (reminder != null) {
                    sendPushNotification(reminder.title, reminder.message);
                    prefs.edit().putBoolean("bill_reminder_notified", true).apply();
                }
            }
        }).start();
    }

    private String getShortMonthName(String date) {
        if (date == null || date.length() < 7) return "";
        String monthNum = date.substring(5, 7);
        switch (monthNum) {
            case "04": return "Kwi";
            case "05": return "Maj";
            case "06": return "Cze";
            default: return "";
        }
    }

    private void loadHomeData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String uid = prefs.getString("user_id", null);

        if (uid == null) {
            tvBalance.setText("Brak danych użytkownika");
            tvMonthlySpending.setText("Brak danych");
            return;
        }

        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(requireContext());
            List<Transaction> transactions = db.transactionDao().getTransactionsForUser(uid);

            Collections.sort(transactions, (t1, t2) -> {
                int dateCmp = t2.getDate().compareTo(t1.getDate());
                if (dateCmp != 0) return dateCmp;
                return Integer.compare(t2.getId(), t1.getId());
            });

            double saldo = 0;
            double monthlySpending = 0;
            String currentMonth = "2025-06";
            for (Transaction t : transactions) {
                saldo += t.getAmount();
                if (t.getDate().startsWith(currentMonth) && t.getAmount() < 0) {
                    monthlySpending += -t.getAmount();
                }
            }
            final double saldoFinal = saldo;
            final double monthlySpendingFinal = monthlySpending;

            final List<Transaction> lastTransactions = transactions.size() > 5 ? transactions.subList(0, 5) : new ArrayList<>(transactions);

            final List<TransactionListItem> items = new ArrayList<>();
            for (Transaction t : lastTransactions) {
                items.add(TransactionListItem.transactionItem(t));
            }

            String[] months = new String[]{"Kwi", "Maj", "Cze"};
            Map<String, Float> incomesPerMonth = new HashMap<>();
            Map<String, Float> outcomesPerMonth = new HashMap<>();
            for (Transaction t : transactions) {
                String month = getShortMonthName(t.getDate());
                float amount = (float) t.getAmount();
                if ("income".equals(t.getType())) {
                    incomesPerMonth.put(month, incomesPerMonth.getOrDefault(month, 0f) + amount);
                } else {
                    outcomesPerMonth.put(month, outcomesPerMonth.getOrDefault(month, 0f) + Math.abs(amount));
                }
            }
            float[] incomeVals = new float[3];
            float[] outcomeVals = new float[3];
            for (int i = 0; i < 3; i++) {
                incomeVals[i] = incomesPerMonth.getOrDefault(months[i], 0f);
                outcomeVals[i] = outcomesPerMonth.getOrDefault(months[i], 0f);
            }
            float max = Math.max(
                    Math.max(incomeVals[0], outcomeVals[0]),
                    Math.max(Math.max(incomeVals[1], outcomeVals[1]), Math.max(incomeVals[2], outcomeVals[2]))
            );
            int maxBarHeight = 120;
            int[] incomeHeights = new int[3];
            int[] outcomeHeights = new int[3];
            for (int i = 0; i < 3; i++) {
                incomeHeights[i] = max > 0 ? Math.round((incomeVals[i] / max) * maxBarHeight) : 0;
                outcomeHeights[i] = max > 0 ? Math.round((outcomeVals[i] / max) * maxBarHeight) : 0;
            }

            requireActivity().runOnUiThread(() -> {
                tvBalance.setText(String.format("%.2f PLN", saldoFinal));
                tvMonthlySpending.setText(String.format("%.2f PLN", monthlySpendingFinal));

                TransactionsAdapter adapter = new TransactionsAdapter();
                adapter.setItems(items);
                adapter.setOnBindViewHolderListener((holder, transaction) -> {
                    TextView tvRecipient = holder.itemView.findViewById(R.id.tvRecipient);
                    TextView tvAmount = holder.itemView.findViewById(R.id.tvAmount);
                    tvRecipient.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                    if (transaction.getAmount() >= 0) {
                        tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.green2));
                    } else {
                        tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.red2));
                    }
                    tvAmount.setTypeface(null, Typeface.BOLD);
                });
                rvRecentTransactions.setAdapter(adapter);

                int green = ContextCompat.getColor(requireContext(), R.color.green2);
                int red = ContextCompat.getColor(requireContext(), R.color.red2);

                if (barMarchIn != null) {
                    barMarchIn.getLayoutParams().height = incomeHeights[0];
                    barMarchIn.setBackgroundColor(green);
                    barMarchIn.requestLayout();
                }
                if (barMarchOut != null) {
                    barMarchOut.getLayoutParams().height = outcomeHeights[0];
                    barMarchOut.setBackgroundColor(red);
                    barMarchOut.requestLayout();
                }
                if (barAprilIn != null) {
                    barAprilIn.getLayoutParams().height = incomeHeights[1];
                    barAprilIn.setBackgroundColor(green);
                    barAprilIn.requestLayout();
                }
                if (barAprilOut != null) {
                    barAprilOut.getLayoutParams().height = outcomeHeights[1];
                    barAprilOut.setBackgroundColor(red);
                    barAprilOut.requestLayout();
                }
                if (barMayIn != null) {
                    barMayIn.getLayoutParams().height = incomeHeights[2];
                    barMayIn.setBackgroundColor(green);
                    barMayIn.requestLayout();
                }
                if (barMayOut != null) {
                    barMayOut.getLayoutParams().height = outcomeHeights[2];
                    barMayOut.setBackgroundColor(red);
                    barMayOut.requestLayout();
                }

                if (labelMarch != null) labelMarch.setText("Kwiecień");
                if (labelApril != null) labelApril.setText("Maj");
                if (labelMay != null) labelMay.setText("Czerwiec");
                if (valueMarchIn != null) valueMarchIn.setText(String.format("+%.2f", incomeVals[0]));
                if (valueMarchOut != null) valueMarchOut.setText(String.format("-%.2f", outcomeVals[0]));
                if (valueAprilIn != null) valueAprilIn.setText(String.format("+%.2f", incomeVals[1]));
                if (valueAprilOut != null) valueAprilOut.setText(String.format("-%.2f", outcomeVals[1]));
                if (valueMayIn != null) valueMayIn.setText(String.format("+%.2f", incomeVals[2]));
                if (valueMayOut != null) valueMayOut.setText(String.format("-%.2f", outcomeVals[2]));

                if (valueMarchIn != null) valueMarchIn.setTextColor(green);
                if (valueMarchOut != null) valueMarchOut.setTextColor(red);
                if (valueAprilIn != null) valueAprilIn.setTextColor(green);
                if (valueAprilOut != null) valueAprilOut.setTextColor(red);
                if (valueMayIn != null) valueMayIn.setTextColor(green);
                if (valueMayOut != null) valueMayOut.setTextColor(red);

                int marginAboveBar = 4; // px, można zmienić na dp jeśli chcesz
                if (valueMarchIn != null) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) valueMarchIn.getLayoutParams();
                    params.bottomMargin = marginAboveBar;
                    valueMarchIn.setLayoutParams(params);
                }
                if (valueAprilIn != null) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) valueAprilIn.getLayoutParams();
                    params.bottomMargin = marginAboveBar;
                    valueAprilIn.setLayoutParams(params);
                }
                if (valueMayIn != null) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) valueMayIn.getLayoutParams();
                    params.bottomMargin = marginAboveBar;
                    valueMayIn.setLayoutParams(params);
                }
            });
        }).start();
    }

    private void sendPushNotification(String title, String message) {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "bills_channel")
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}