package com.example.financeapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.UserDao;
import com.example.financeapp.ui.database.TransactionDao;
import com.example.financeapp.ui.database.BillReminderDao;
import com.example.financeapp.ui.database.CategoryLimitDao;
import com.example.financeapp.ui.models.User;
import com.example.financeapp.ui.models.Transaction;
import com.example.financeapp.ui.models.BillReminder;
import com.example.financeapp.ui.models.CategoryLimit;
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

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean userInit = prefs.getBoolean("user_initialized", false);
        if (!userInit) {
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                UserDao userDao = db.userDao();
                TransactionDao transactionDao = db.transactionDao();
                BillReminderDao billReminderDao = db.billReminderDao();
                CategoryLimitDao categoryLimitDao = db.categoryLimitDao();

                User user1 = new User("testUidJan", "jan.kowalski@bank.pl", "Jan", "Kowalski");
                userDao.insert(user1);
                String userId1 = user1.getUid();

                User user2 = new User("testUidAnna", "anna.nowak@bank.pl", "Anna", "Nowak");
                userDao.insert(user2);
                String userId2 = user2.getUid();

                // Transakcje dla Jan Kowalski
                transactionDao.insert(new Transaction(userId1, "Biedronka", -120.55, "Zakupy", "2025-06-04", "expense"));
                transactionDao.insert(new Transaction(userId1, "Allegro", -89.99, "Rozrywka", "2025-06-03", "expense"));
                transactionDao.insert(new Transaction(userId1, "PKP Intercity", -59.00, "Transport", "2025-06-02", "expense"));
                transactionDao.insert(new Transaction(userId1, "Praca", 3500.00, "Wynagrodzenie", "2025-05-28", "income"));
                transactionDao.insert(new Transaction(userId1, "Lidl", -77.30, "Zakupy", "2025-05-27", "expense"));
                transactionDao.insert(new Transaction(userId1, "ZUS", -400.00, "Rachunki", "2025-05-15", "expense"));
                transactionDao.insert(new Transaction(userId1, "Zwrot Allegro", 49.00, "Zwrot", "2025-05-10", "income"));
                transactionDao.insert(new Transaction(userId1, "Orlen", -230.00, "Transport", "2025-05-08", "expense"));
                transactionDao.insert(new Transaction(userId1, "Multikino", -65.00, "Rozrywka", "2025-05-02", "expense"));
                transactionDao.insert(new Transaction(userId1, "Dentysta", -250.00, "Zdrowie", "2025-04-29", "expense"));

                // Limity kategorii dla Jan Kowalski
                categoryLimitDao.insert(new CategoryLimit(userId1, "Zakupy", 600.0));
                categoryLimitDao.insert(new CategoryLimit(userId1, "Rozrywka", 200.0));
                categoryLimitDao.insert(new CategoryLimit(userId1, "Transport", 300.0));
                categoryLimitDao.insert(new CategoryLimit(userId1, "Rachunki", 500.0));

                // Przypomnienia dla Jan Kowalski
                billReminderDao.insert(new BillReminder(userId1, "Rachunek za prąd", "Opłać rachunek za prąd do końca miesiąca", "2025-06-25", false, System.currentTimeMillis() + 60_000));
                billReminderDao.insert(new BillReminder(userId1, "Rachunek za internet", "Faktura za internet UPC", "2025-06-20", false, System.currentTimeMillis() + 120_000));
                billReminderDao.insert(new BillReminder(userId1, "Telefon", "Zapłać rachunek za telefon", "2025-06-17", false, System.currentTimeMillis() + 180_000));

                // Transakcje dla Anna Nowak
                transactionDao.insert(new Transaction(userId2, "Rossmann", -75.50, "Zakupy", "2025-06-03", "expense"));
                transactionDao.insert(new Transaction(userId2, "Netflix", -52.00, "Rozrywka", "2025-06-02", "expense"));
                transactionDao.insert(new Transaction(userId2, "Praca", 4200.00, "Wynagrodzenie", "2025-05-29", "income"));
                transactionDao.insert(new Transaction(userId2, "ZTM", -110.00, "Transport", "2025-05-21", "expense"));
                transactionDao.insert(new Transaction(userId2, "Pizza Hut", -82.00, "Jedzenie", "2025-05-19", "expense"));
                transactionDao.insert(new Transaction(userId2, "Zwrot Zalando", 120.00, "Zwrot", "2025-05-09", "income"));
                transactionDao.insert(new Transaction(userId2, "PGNiG", -220.00, "Rachunki", "2025-05-05", "expense"));
                transactionDao.insert(new Transaction(userId2, "Uber", -40.00, "Transport", "2025-04-30", "expense"));
                transactionDao.insert(new Transaction(userId2, "McDonald's", -32.00, "Jedzenie", "2025-04-28", "expense"));
                transactionDao.insert(new Transaction(userId2, "Empik", -120.00, "Rozrywka", "2025-04-25", "expense"));

                // Limity kategorii dla Anna Nowak
                categoryLimitDao.insert(new CategoryLimit(userId2, "Zakupy", 400.0));
                categoryLimitDao.insert(new CategoryLimit(userId2, "Jedzenie", 300.0));
                categoryLimitDao.insert(new CategoryLimit(userId2, "Rozrywka", 250.0));
                categoryLimitDao.insert(new CategoryLimit(userId2, "Transport", 200.0));

                // Przypomnienia dla Anna Nowak
                billReminderDao.insert(new BillReminder(userId2, "Rachunek za gaz", "Nie zapomnij o gazie!", "2025-06-15", false, System.currentTimeMillis() + 240_000));
                billReminderDao.insert(new BillReminder(userId2, "Internet", "Faktura za internet", "2025-06-22", false, System.currentTimeMillis() + 300_000));
                billReminderDao.insert(new BillReminder(userId2, "Telekom", "Zapłać telefon", "2025-06-13", false, System.currentTimeMillis() + 360_000));

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