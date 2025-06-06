package com.example.financeapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private Spinner spinnerCategories;
    private View categoryChangeBtn;
    private Transaction currentTransaction;
    private int transactionId;
    private String userId;
    private boolean isSpinnerInitialized = false;

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
        categoryChangeBtn = view.findViewById(R.id.text_category_change);
        spinnerCategories = view.findViewById(R.id.spinner_categories);


        ImageView backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        transactionId = getArguments() != null ? getArguments().getInt("transactionId", -1) : -1;

        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", String.valueOf(-1));

        viewModel = new ViewModelProvider(this).get(TransactionDetailsViewModel.class);
        TransactionRepository repository = new TransactionRepository(AppDatabase.getDatabase(requireContext()).transactionDao());
        viewModel.setRepository(repository);

        viewModel.getTransactionById(transactionId, userId).observe(getViewLifecycleOwner(), this::updateUI);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.categories_array,
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerCategories.setAdapter(adapter);

        categoryChangeBtn.setOnClickListener(v -> {
            if (spinnerCategories.getVisibility() == View.VISIBLE) {
                spinnerCategories.setVisibility(View.GONE);
            } else {
                spinnerCategories.setSelection(0, false);
                isSpinnerInitialized = false;
                spinnerCategories.setVisibility(View.VISIBLE);
            }
        });

        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View itemView, int position, long id) {
                if (!isSpinnerInitialized) {
                    isSpinnerInitialized = true;
                    return;
                }
                String selectedCategory = (String) parent.getItemAtPosition(position);
                if (currentTransaction != null && !selectedCategory.equals("Wybierz kategorię")) {
                    currentTransaction.setCategory(selectedCategory);
                    viewModel.updateTransaction(currentTransaction);
                    category.setText(selectedCategory);
                    Toast.makeText(requireContext(), "Zmieniono kategorię na: " + selectedCategory, Toast.LENGTH_SHORT).show();
                    spinnerCategories.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        return view;
    }

    private void updateUI(Transaction transaction) {
        if (transaction != null) {
            currentTransaction = transaction;
            recipient.setText(transaction.getRecipient());
            amount.setText(String.format("%.2f PLN", transaction.getAmount()));
            date.setText(transaction.getDate());
            category.setText(transaction.getCategory());
            type.setText(transaction.getType().equals("income") ? "Przychód" : "Wydatek");

            String[] categories = getResources().getStringArray(R.array.categories_array);
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(transaction.getCategory())) {
                    spinnerCategories.setSelection(i);
                    break;
                }
            }
        }
    }
}