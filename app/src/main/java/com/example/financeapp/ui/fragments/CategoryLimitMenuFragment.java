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

public class CategoryLimitMenuFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_limit_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnSet = view.findViewById(R.id.btnSetLimit);
        Button btnShow = view.findViewById(R.id.btnShowUsage);

        btnSet.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_menu_to_setLimit));
        btnShow.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_menu_to_showLimitUsage));
    }
}