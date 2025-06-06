package com.example.financeapp.ui.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.NavController;
import androidx.work.*;
import com.example.financeapp.R;
import com.example.financeapp.ui.ViewModels.BillReminderViewModel;
import com.example.financeapp.ui.ViewModels.BillReminderViewModelFactory;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.BillReminderRepository;
import com.example.financeapp.ui.models.BillReminder;
import com.example.financeapp.ui.utils.BillNotificationWorker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddBillReminderFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_bill_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        EditText etTitle = view.findViewById(R.id.etTitle);
        EditText etMessage = view.findViewById(R.id.etMessage);
        EditText etAmount = view.findViewById(R.id.etAmount); // NOWY EditText
        EditText etDueDate = view.findViewById(R.id.etDueDate);
        Button btnSave = view.findViewById(R.id.btnSaveReminder);
        ImageView btnBack = view.findViewById(R.id.btn_back_reminder);

        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.navigation_profile);
        });

        etDueDate.setFocusable(false);
        etDueDate.setClickable(true);
        etDueDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(getContext(),
                    (view1, year, month, dayOfMonth) -> {
                        String dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        etDueDate.setText(dateStr);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });

        BillReminderRepository repo = new BillReminderRepository(AppDatabase.getDatabase(requireContext()).billReminderDao());
        BillReminderViewModelFactory factory = new BillReminderViewModelFactory(repo);
        BillReminderViewModel viewModel = new ViewModelProvider(requireActivity(), factory).get(BillReminderViewModel.class);

        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", "-1");

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String message = etMessage.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String dueDate = etDueDate.getText().toString().trim();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(message) || TextUtils.isEmpty(dueDate) || TextUtils.isEmpty(amountStr)) {
                Toast.makeText(getContext(), "Wypełnij wszystkie pola!", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = 0.0;
            try {
                amount = Double.parseDouble(amountStr.replace(",", "."));
            } catch (Exception e) {
                Toast.makeText(getContext(), "Podaj poprawną kwotę!", Toast.LENGTH_SHORT).show();
                return;
            }

            long notifTime = 0;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(dueDate));
                cal.add(Calendar.DAY_OF_MONTH, -1);
                notifTime = cal.getTimeInMillis();
            } catch (Exception ignored) {}

            BillReminder reminder = new BillReminder();
            reminder.userId = userId;
            reminder.title = title;
            reminder.message = message;
            reminder.dueDate = dueDate;
            reminder.paid = false;
            reminder.notificationTime = notifTime;
            reminder.amount = amount;

            viewModel.insert(reminder);

            Toast.makeText(getContext(), "Dodano przypomnienie!", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();

            long delay = reminder.notificationTime - System.currentTimeMillis();
            if (delay < 0) delay = 0;
            WorkManager.getInstance(requireContext()).enqueue(
                    new OneTimeWorkRequest.Builder(BillNotificationWorker.class)
                            .setInitialDelay(delay, java.util.concurrent.TimeUnit.MILLISECONDS)
                            .setInputData(new Data.Builder().putInt("reminderId", reminder.id).build())
                            .build()
            );
        });
    }
}