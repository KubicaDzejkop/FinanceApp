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
import android.widget.EditText;
import android.widget.Toast;
import com.example.financeapp.ui.ViewModels.LoginViewModel;
import androidx.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class LoginFragment extends Fragment {
    private LoginViewModel loginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_panel, container, false); // zakładam, że masz już nowy layout
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        EditText usernameEt = view.findViewById(R.id.editTextUsername);
        EditText passwordEt = view.findViewById(R.id.editTextPassword);
        Button loginButton = view.findViewById(R.id.button_login);

        loginButton.setOnClickListener(v -> {
            String username = usernameEt.getText().toString();
            String password = passwordEt.getText().toString();

            loginViewModel.login(username, password).observe(getViewLifecycleOwner(), user -> {
                Log.d("Login", "Próba loginu: " + username + " / " + password + " -> " + (user != null));
                if (user != null) {
                    // ZAPISZ userId do SharedPreferences
                    SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                    prefs.edit().putInt("user_id", user.getId()).apply();

                    // Przejście do HomeFragment
                    Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);
                } else {
                    Toast.makeText(getContext(), "Nieprawidłowy login lub hasło", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}