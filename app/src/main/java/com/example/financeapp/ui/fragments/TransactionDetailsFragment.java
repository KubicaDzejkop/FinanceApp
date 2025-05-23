package com.example.financeapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.financeapp.R;
import com.example.financeapp.ui.ViewModels.TransactionDetailsViewModel;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.TransactionRepository;
import com.example.financeapp.ui.models.Transaction;

public class TransactionDetailsFragment extends Fragment {

    private TransactionDetailsViewModel viewModel;
    private TextView recipient, amount, date, category, type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_details, container, false);
        recipient = view.findViewById(R.id.text_recipient);
        amount = view.findViewById(R.id.text_amount);
        date = view.findViewById(R.id.text_date);
        category = view.findViewById(R.id.text_operation_category);
        type = view.findViewById(R.id.text_operation_type);

        // Obsługa cofania strzałką
        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            // Cofnij do historii (popBackStack cofa do ostatniego fragmentu)
            Navigation.findNavController(view).popBackStack();
        });

        // Odczytaj przekazane argumenty
        int transactionId = getArguments() != null ? getArguments().getInt("transactionId", -1) : -1;

        // Odczytaj userId z SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", String.valueOf(-1));

        // Zainicjalizuj ViewModel i Repozytorium
        viewModel = new ViewModelProvider(this).get(TransactionDetailsViewModel.class);
        TransactionRepository repository = new TransactionRepository(AppDatabase.getDatabase(requireContext()).transactionDao());
        viewModel.setRepository(repository);

        // Obserwuj dane transakcji
        viewModel.getTransactionById(transactionId, userId).observe(getViewLifecycleOwner(), this::updateUI);

        return view;
    }

    private void updateUI(Transaction transaction) {
        if (transaction != null) {
            recipient.setText(transaction.getRecipient());
            amount.setText(String.format("%.2f PLN", transaction.getAmount()));
            date.setText(transaction.getDate());
            category.setText(transaction.getCategory());
            type.setText(transaction.getType().equals("income") ? "Przychód" : "Wydatek");
        }
    }
}