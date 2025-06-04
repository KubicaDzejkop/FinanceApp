package com.example.financeapp.ui.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
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

public class BillReminderDetailFragment extends Fragment {
    private static final String ARG_REMINDER_ID = "reminderId";

    public static BillReminderDetailFragment newInstance(int reminderId) {
        BillReminderDetailFragment f = new BillReminderDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_REMINDER_ID, reminderId);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bill_reminder_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        int reminderId = getArguments() != null ? getArguments().getInt(ARG_REMINDER_ID) : -1;
        BillReminderRepository repo = new BillReminderRepository(AppDatabase.getDatabase(requireContext()).billReminderDao());
        BillReminderViewModelFactory factory = new BillReminderViewModelFactory(repo);
        BillReminderViewModel viewModel = new ViewModelProvider(requireActivity(), factory).get(BillReminderViewModel.class);

        TextView tvTitle = view.findViewById(R.id.tvDetailTitle);
        TextView tvDue = view.findViewById(R.id.tvDetailDue);
        TextView tvStatus = view.findViewById(R.id.tvDetailStatus);
        TextView tvMessage = view.findViewById(R.id.tvDetailMessage);
        Button btnPaid = view.findViewById(R.id.btnMarkPaid);

        viewModel.getById(reminderId).observe(getViewLifecycleOwner(), reminder -> {
            if (reminder == null) return;
            tvTitle.setText(reminder.title);
            tvDue.setText(reminder.dueDate);
            tvStatus.setText(reminder.paid ? "Opłacony" : "Nieopłacony");
            tvMessage.setText(reminder.message);
            btnPaid.setVisibility(reminder.paid ? View.GONE : View.VISIBLE);

            btnPaid.setOnClickListener(v -> {
                reminder.paid = true;
                viewModel.update(reminder);
                Toast.makeText(requireContext(), "Rachunek oznaczony jako opłacony", Toast.LENGTH_SHORT).show();
                tvStatus.setText("Opłacony");
                btnPaid.setVisibility(View.GONE);
            });
        });
    }
}