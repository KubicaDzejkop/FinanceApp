package com.example.financeapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.financeapp.R;

public class AnalysisFragment extends Fragment {

    private String source = "home"; // domyślnie strona główna

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analysis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Pobierz argument
        if (getArguments() != null && getArguments().getString("source") != null) {
            source = getArguments().getString("source");
        }

        ImageView btnBack = view.findViewById(R.id.btn_back_analysis);
        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            if ("history".equals(source)) {
                navController.navigate(R.id.navigation_history);
            } else {
                navController.navigate(R.id.navigation_home);
            }
        });
    }
}