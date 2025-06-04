package com.example.financeapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.financeapp.R;
import com.example.financeapp.databinding.FragmentProfileBinding;
import com.example.financeapp.ui.ViewModels.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import androidx.navigation.Navigation;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        profileViewModel.getProfileSummary().observe(getViewLifecycleOwner(), summary -> {
            binding.textProfile.setText(summary);
        });

        binding.tabMessages.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_billReminderListFragment);
        });


        binding.tabLimits.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_categoryLimitMenuFragment);
        });

        binding.buttonAddReminder.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.addBillReminderFragment);
        });


        binding.buttonLogout.setOnClickListener(v -> {
            logout();
        });

        return binding.getRoot();
    }

    private void logout() {

        FirebaseAuth.getInstance().signOut();


        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().remove("user_id").apply();


        Navigation.findNavController(requireView())
                .navigate(R.id.action_profileFragment_to_loginFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}