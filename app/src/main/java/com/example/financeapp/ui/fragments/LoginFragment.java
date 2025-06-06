package com.example.financeapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.financeapp.MainActivity;
import com.example.financeapp.R;
import com.example.financeapp.databinding.FragmentLoginPanelBinding;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.models.User;
import com.example.financeapp.ui.models.Transaction;
import com.example.financeapp.ui.models.BillReminder;
import com.example.financeapp.ui.models.CategoryLimit;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    private boolean isPasswordVisible = false;
    private FragmentLoginPanelBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginPanelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        binding.imageViewShowPassword.setOnClickListener(v -> {
            if (!isPasswordVisible) {
                binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.imageViewShowPassword.setImageResource(R.drawable.password_eye_off);
            } else {
                binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.imageViewShowPassword.setImageResource(R.drawable.password_eye);
            }
            binding.editTextPassword.setSelection(binding.editTextPassword.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        // LOGIN
        binding.buttonLogin.setOnClickListener(v -> {
            String email = binding.editTextUsername.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Podaj e-mail i hasło", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();
                                SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                                prefs.edit().putString("user_id", uid)
                                        .putBoolean("bill_reminder_notified", false)
                                        .apply();
                            }
                            Toast.makeText(getContext(), "Zalogowano!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);
                        } else {
                            Toast.makeText(getContext(), "Nieprawidłowy login lub hasło", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // REJESTRACJA
        binding.buttonRegister.setOnClickListener(v -> {
            String email = binding.editTextUsername.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Podaj e-mail i hasło", Toast.LENGTH_SHORT).show();
                return;
            }

            String firstName = "Użytkownik";
            String lastName = "";

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();

                                SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                                prefs.edit().putString("user_id", uid)
                                        .putString("first_name", firstName)
                                        .putString("last_name", lastName)
                                        .putBoolean("bill_reminder_notified", false)
                                        .apply();

                                // Dodaj przykładowe dane do Room – dla KAŻDEGO nowego konta!
                                new Thread(() -> {
                                    AppDatabase db = AppDatabase.getDatabase(requireContext());
                                    User profile = new User(uid, email, firstName, lastName);
                                    db.userDao().insert(profile);

                                    // MARZEC 2025
                                    db.transactionDao().insert(new Transaction(uid, "Freshmarket", -45.25, "Zakupy", "2025-03-02", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "Circle K", -90.00, "Transport", "2025-03-04", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "Netflix", -29.00, "Rozrywka", "2025-03-06", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "Przelew od rodziny", 500.00, "Prezent", "2025-03-09", "income"));
                                    db.transactionDao().insert(new Transaction(uid, "Energa", -180.50, "Rachunki", "2025-05-08", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "H&M", -120.00, "Zakupy", "2025-03-17", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "Starbucks", -25.40, "Jedzenie", "2025-03-22", "expense"));

                                    // KWIECIEŃ 2025
                                    db.transactionDao().insert(new Transaction(uid, "Żabka", -53.25, "Zakupy", "2025-04-03", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "Shell", -120.00, "Transport", "2025-04-04", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "Spotify", -19.99, "Rozrywka", "2025-04-07", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "Pensja", 3400.00, "Wynagrodzenie", "2025-04-05", "income"));
                                    db.transactionDao().insert(new Transaction(uid, "PGNiG", -210.50, "Rachunki", "2025-04-14", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "CCC", -89.00, "Zakupy", "2025-04-18", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "Cinema City", -44.00, "Rozrywka", "2025-04-19", "expense"));

                                    // MAJ 2025
                                    db.transactionDao().insert(new Transaction(uid, "Carrefour", -99.90, "Zakupy", "2025-05-02", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "Uber", -37.50, "Transport", "2025-05-03", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "Vinted", 250.00, "Sprzedaż", "2025-05-06", "income"));
                                    db.transactionDao().insert(new Transaction(uid, "Bonus", 400.00, "Premia", "2025-05-09", "income"));
                                    db.transactionDao().insert(new Transaction(uid, "MediaMarkt", -699.00, "Elektronika", "2025-05-11", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "Play", -60.00, "Telefon", "2025-05-17", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "Pizza Hut", -120.00, "Jedzenie", "2025-05-21", "expense"));

                                    // CZERWIEC 2025 (1-5 czerwca, 7 transakcji)
                                    db.transactionDao().insert(new Transaction(uid, "Auchan", -140.00, "Zakupy", "2025-06-01", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "Rossmann", -70.00, "Drogeria", "2025-06-01", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "PKP TLK", -60.00, "Transport", "2025-06-02", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "Wypłata", 3700.00, "Wynagrodzenie", "2025-06-02", "income"));
                                    db.transactionDao().insert(new Transaction(uid, "Empik", -105.00, "Książki", "2025-06-04", "expense"));
                                    db.transactionDao().insert(new Transaction(uid, "Kebab King", -52.40, "Jedzenie", "2025-06-05", "expense"));

                                    // Przykładowe limity kategorii
                                    db.categoryLimitDao().insert(new CategoryLimit(uid, "Zakupy", 600.0));
                                    db.categoryLimitDao().insert(new CategoryLimit(uid, "Rozrywka", 200.0));
                                    db.categoryLimitDao().insert(new CategoryLimit(uid, "Transport", 400.0));
                                    db.categoryLimitDao().insert(new CategoryLimit(uid, "Rachunki", 1000.0));
                                    db.categoryLimitDao().insert(new CategoryLimit(uid, "Jedzenie", 400.0));
                                    db.categoryLimitDao().insert(new CategoryLimit(uid, "Zdrowie", 300.0));
                                    db.categoryLimitDao().insert(new CategoryLimit(uid, "Inne", 400.0));
                                    db.categoryLimitDao().insert(new CategoryLimit(uid, "Żywność", 400.0));

                                }).start();
                            }
                            Toast.makeText(getContext(), "Zarejestrowano i zalogowano!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);
                        } else {
                            Toast.makeText(getContext(), "Błąd rejestracji: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}