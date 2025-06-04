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

import com.example.financeapp.R;
import com.example.financeapp.ui.adapters.BillReminderAdapter;
import com.example.financeapp.ui.ViewModels.BillReminderViewModel;
import com.example.financeapp.ui.ViewModels.BillReminderViewModelFactory;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.BillReminderRepository;

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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", "-1");

        BillReminderRepository repo = new BillReminderRepository(AppDatabase.getDatabase(requireContext()).billReminderDao());
        BillReminderViewModelFactory factory = new BillReminderViewModelFactory(repo);
        viewModel = new ViewModelProvider(this, factory).get(BillReminderViewModel.class);

        viewModel.getAll(userId).observe(getViewLifecycleOwner(), reminders -> {
            adapter.setData(reminders);
        });
    }
}