package com.example.financeapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.financeapp.databinding.FragmentProfileBinding;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.models.User;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        // Pobierz dane usera z Room na podstawie UID (z SharedPreferences)
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String uid = prefs.getString("user_id", null);

        if (uid != null) {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getDatabase(requireContext());
                User user = db.userDao().getUserByUid(uid);
                String name = (user != null)
                        ? user.getFirstName() + " " + user.getLastName()
                        : "Brak danych";
                requireActivity().runOnUiThread(() -> binding.textProfileName.setText(name));
            }).start();

            // BADGE: policz ile jest nieprzeczytanych wiadomości (nieopłaconych przypomnień)
            new Thread(() -> {
                AppDatabase db = AppDatabase.getDatabase(requireContext());
                int unreadCount = db.billReminderDao().countUnpaid(uid);
                requireActivity().runOnUiThread(() -> {
                    TextView badge = binding.badgeMessages;
                    if (unreadCount > 0) {
                        badge.setText(String.valueOf(unreadCount));
                        badge.setVisibility(View.VISIBLE);
                    } else {
                        badge.setVisibility(View.GONE);
                    }
                });
            }).start();
        } else {
            // Jeśli nie zalogowany, nie pokazuj badge
            binding.badgeMessages.setVisibility(View.GONE);
        }

        // Zakładka "Wiadomości"
        binding.tabMessages.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(
                    com.example.financeapp.R.id.action_profileFragment_to_billReminderListFragment);
        });

        // Zakładka "Limity"
        binding.tabLimits.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(
                    com.example.financeapp.R.id.action_profileFragment_to_categoryLimitMenuFragment);
        });

        // Przycisk "Dodaj przypomnienie"
        binding.buttonAddReminder.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(
                    com.example.financeapp.R.id.addBillReminderFragment);
        });

        // Przycisk "Wyloguj się"
        binding.buttonLogout.setOnClickListener(v -> {
            logout();
        });

        // Zakładka/Opcja "Oceń aplikacje"
        binding.tabRate.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(
                    com.example.financeapp.R.id.action_profileFragment_to_rateAppFragment);
        });

        return binding.getRoot();
    }

    private void logout() {
        // Wylogowanie z Firebase i usunięcie UID z SharedPreferences
        FirebaseAuth.getInstance().signOut();
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().remove("user_id").apply();
        Navigation.findNavController(requireView())
                .navigate(com.example.financeapp.R.id.action_profileFragment_to_loginFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}