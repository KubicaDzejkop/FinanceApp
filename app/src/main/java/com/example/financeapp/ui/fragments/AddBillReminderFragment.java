package com.example.financeapp.ui.fragments;

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
import androidx.work.*;

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
        EditText etDueDate = view.findViewById(R.id.etDueDate);
        Button btnSave = view.findViewById(R.id.btnSaveReminder);

        BillReminderRepository repo = new BillReminderRepository(AppDatabase.getDatabase(requireContext()).billReminderDao());
        BillReminderViewModelFactory factory = new BillReminderViewModelFactory(repo);
        BillReminderViewModel viewModel = new ViewModelProvider(requireActivity(), factory).get(BillReminderViewModel.class);

        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", "-1");

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String message = etMessage.getText().toString().trim();
            String dueDate = etDueDate.getText().toString().trim();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(message) || TextUtils.isEmpty(dueDate)) {
                Toast.makeText(getContext(), "Wypełnij wszystkie pola!", Toast.LENGTH_SHORT).show();
                return;
            }

            long notifTime = 0;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(dueDate));
                cal.add(Calendar.DAY_OF_MONTH, -1); // dzień przed
                notifTime = cal.getTimeInMillis();
            } catch (Exception ignored) {}

            BillReminder reminder = new BillReminder();
            reminder.userId = userId;
            reminder.title = title;
            reminder.message = message;
            reminder.dueDate = dueDate;
            reminder.paid = false;
            reminder.notificationTime = notifTime;

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