package com.example.financeapp.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.financeapp.R;
import com.example.financeapp.ui.database.TransactionDao;
import com.example.financeapp.ui.models.Transaction;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.database.TransactionRepository;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisFragment extends Fragment {

    private String source = "home";

    private BarChart barChart;
    private TextView textAvgSpending, textTotalIncomes, textTotalOutcomes;
    private TransactionRepository repository;
    private String userId;

    // Miesiące do wyświetlenia na wykresie
    private final String[] months = new String[]{"Lut", "Mar", "Kwi", "Maj"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analysis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


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


        barChart = view.findViewById(R.id.barChart);
        textAvgSpending = view.findViewById(R.id.text_avg_spending);
        textTotalIncomes = view.findViewById(R.id.text_total_incomes);
        textTotalOutcomes = view.findViewById(R.id.text_total_outcomes);


        userId = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("user_id", "-1");


        TransactionDao dao = AppDatabase.getDatabase(requireContext()).transactionDao();
        repository = new TransactionRepository(dao);


        repository.getAllTransactions(userId).observe(getViewLifecycleOwner(), transactions -> {
            if (transactions == null || transactions.isEmpty()) {
                return;
            }
            updateAnalysis(transactions);
        });
    }

    private void updateAnalysis(List<Transaction> transactions) {
        Map<String, Float> incomesPerMonth = new HashMap<>();
        Map<String, Float> outcomesPerMonth = new HashMap<>();

        float totalOutcomes = 0f;
        float totalIncomes = 0f;

        for (Transaction t : transactions) {
            String month = getShortMonthName(t.getDate());
            float amount = (float) t.getAmount();
            if (t.getType().equals("income")) {
                totalIncomes += amount;
                incomesPerMonth.put(month, incomesPerMonth.getOrDefault(month, 0f) + amount);
            } else {
                totalOutcomes += Math.abs(amount);
                outcomesPerMonth.put(month, outcomesPerMonth.getOrDefault(month, 0f) + Math.abs(amount));
            }
        }


        int monthsWithOutcomes = 0;
        for (String m : months) {
            if (outcomesPerMonth.getOrDefault(m, 0f) > 0.01f) monthsWithOutcomes++;
        }
        float avgSpending = monthsWithOutcomes > 0 ? totalOutcomes / monthsWithOutcomes : 0f;

        DecimalFormat df = new DecimalFormat("#,##0.00");

        textAvgSpending.setText(df.format(Math.abs(avgSpending)) + " PLN");
        textTotalIncomes.setText(df.format(totalIncomes) + " PLN");
        textTotalOutcomes.setText(df.format(Math.abs(totalOutcomes)) + " PLN");


        List<BarEntry> entriesIncome = new ArrayList<>();
        List<BarEntry> entriesOutcome = new ArrayList<>();
        for (int i = 0; i < months.length; i++) {
            float income = incomesPerMonth.getOrDefault(months[i], 0f);
            float outcome = outcomesPerMonth.getOrDefault(months[i], 0f); // wydatki zawsze dodatnie
            entriesIncome.add(new BarEntry(i, income));
            entriesOutcome.add(new BarEntry(i, outcome));
        }


        BarDataSet setIncome = new BarDataSet(entriesIncome, "Wpływy");
        setIncome.setColor(getResources().getColor(R.color.green2, null)); // zamień na swój kolor!
        BarDataSet setOutcome = new BarDataSet(entriesOutcome, "Wydatki");
        setOutcome.setColor(getResources().getColor(R.color.red2, null)); // zamień na swój kolor!

        setIncome.setValueTextSize(12f);
        setIncome.setValueTextColor(Color.BLACK);

        setOutcome.setValueTextSize(12f);
        setOutcome.setValueTextColor(Color.BLACK);

        BarData data = new BarData(setIncome, setOutcome);
        data.setBarWidth(0.4f);
        barChart.setData(data);


        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < months.length) {
                    return months[index];
                } else {
                    return "";
                }
            }
        });
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.setFitBars(true);
        barChart.invalidate();


        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(14f);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(18f);
        legend.setXEntrySpace(24f);
        legend.setFormToTextSpace(12f);
        legend.setYOffset(8f);

    }


    private String getShortMonthName(String date) {
        if (date == null || date.length() < 7) return "";
        String monthNum = date.substring(5, 7);
        switch (monthNum) {
            case "02": return "Lut";
            case "03": return "Mar";
            case "04": return "Kwi";
            case "05": return "Maj";
            default: return "";
        }
    }
}
