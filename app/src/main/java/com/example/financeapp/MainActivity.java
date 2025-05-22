package com.example.financeapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.UserDao;
import com.example.financeapp.ui.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.financeapp.databinding.ActivityMainBinding;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = binding.bottomNavigationView;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_history, R.id.navigation_profile)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.loginFragment) {
                navView.setVisibility(View.GONE);
            } else {
                navView.setVisibility(View.VISIBLE);
            }
        });

        // Dodaj usera tylko raz (np. przy pierwszym uruchomieniu apki)
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean userInit = prefs.getBoolean("user_initialized", false);
        if (!userInit) {
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                UserDao userDao = db.userDao();
                userDao.insert(new User("user", "123"));
                prefs.edit().putBoolean("user_initialized", true).apply();
            });
        }
    }
}