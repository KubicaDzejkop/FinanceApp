package com.example.financeapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.financeapp.R;

public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Wymagany pusty konstruktor publiczny
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button loginButton = view.findViewById(R.id.button_login);
        loginButton.setOnClickListener(v -> {
            // Tutaj możesz dodać logikę do weryfikacji użytkownika, jeśli chcesz

            // Przejście do ekranu głównego po kliknięciu
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);
        });
    }
}