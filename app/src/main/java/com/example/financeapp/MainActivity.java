package com.example.financeapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.UserDao;
import com.example.financeapp.ui.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
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
                R.id.navigation_home, R.id.navigation_history, R.id.navigation_profile
        ).build();
        NavigationUI.setupWithNavController(navView, navController);


        navView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int destinationId = item.getItemId();
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.mobile_navigation, true)
                        .build();
                // Jeżeli już jesteś na tym ekranie, nie rób nic.
                if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == destinationId) {
                    return true;
                }
                navController.navigate(destinationId, null, navOptions);
                return true;
            }
        });

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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}