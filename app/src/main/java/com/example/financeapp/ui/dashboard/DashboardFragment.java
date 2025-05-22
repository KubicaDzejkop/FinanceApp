package com.example.financeapp.ui.dashboard;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.financeapp.databinding.FragmentDashboardBinding;
import com.example.financeapp.ui.MainApplication;
import com.example.financeapp.ui.database.TransactionRepository;


public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private TransactionViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        MainApplication app = (MainApplication) requireActivity().getApplication();
        TransactionRepository repository = app.getRepository();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new TransactionViewModel(repository);
            }
        }).get(TransactionViewModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}