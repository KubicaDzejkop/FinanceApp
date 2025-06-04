package com.example.financeapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.financeapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    private boolean isPasswordVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_panel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        EditText usernameEt = view.findViewById(R.id.editTextUsername);
        EditText passwordEt = view.findViewById(R.id.editTextPassword);
        Button loginButton = view.findViewById(R.id.button_login);
        Button registerButton = view.findViewById(R.id.button_register);
        ImageView showPasswordIcon = view.findViewById(R.id.imageViewShowPassword);

        if (showPasswordIcon != null) {
            showPasswordIcon.setOnClickListener(v -> {
                if (!isPasswordVisible) {
                    passwordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showPasswordIcon.setImageResource(R.drawable.password_eye_off);
                } else {
                    passwordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPasswordIcon.setImageResource(R.drawable.password_eye);
                }
                passwordEt.setSelection(passwordEt.getText().length());
                isPasswordVisible = !isPasswordVisible;
            });
        }

        loginButton.setOnClickListener(v -> {
            String email = usernameEt.getText().toString().trim();
            String password = passwordEt.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Podaj e-mail i hasło", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                                prefs.edit().putString("user_id", user.getUid()).apply();
                            }
                            Toast.makeText(getContext(), "Zalogowano!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);
                        } else {
                            Toast.makeText(getContext(), "Nieprawidłowy login lub hasło", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        if (registerButton != null) {
            registerButton.setOnClickListener(v -> {
                String email = usernameEt.getText().toString().trim();
                String password = passwordEt.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getContext(), "Podaj e-mail i hasło", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                                    prefs.edit().putString("user_id", user.getUid()).apply();
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
}