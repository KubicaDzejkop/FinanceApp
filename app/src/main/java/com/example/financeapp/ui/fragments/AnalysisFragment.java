package com.example.financeapp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.financeapp.R;
import com.example.financeapp.ui.models.Transaction;
import com.example.financeapp.ui.database.AppDatabase;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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
    private String userId;
    private final String[] months = new String[]{"Mar", "Kwi", "Maj", "Cze"};

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

        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(requireContext());
            List<Transaction> transactions = db.transactionDao().getTransactionsForUser(userId);

            if (transactions == null || transactions.isEmpty()) return;

            Map<String, Float> incomesPerMonth = new HashMap<>();
            Map<String, Float> outcomesPerMonth = new HashMap<>();

            float totalOutcomes = 0f;
            float totalIncomes = 0f;

            for (Transaction t : transactions) {
                String month = getShortMonthName(t.getDate());
                float amount = (float) t.getAmount();
                if ("income".equals(t.getType())) {
                    totalIncomes += amount;
                    incomesPerMonth.put(month, incomesPerMonth.getOrDefault(month, 0f) + amount);
                } else {
                    totalOutcomes += Math.abs(amount);
                    outcomesPerMonth.put(month, outcomesPerMonth.getOrDefault(month, 0f) + Math.abs(amount));
                }
            }

            int nonZeroMonths = 0;
            float outcomeSumForAvg = 0f;
            for (String m : months) {
                float v = outcomesPerMonth.getOrDefault(m, 0f);
                if (v > 0f) {
                    outcomeSumForAvg += v;
                    nonZeroMonths++;
                }
            }
            float avgSpending = nonZeroMonths > 0 ? (outcomeSumForAvg / nonZeroMonths) : 0f;

            DecimalFormat df = new DecimalFormat("#,##0.00");

            List<BarEntry> entriesStacked = new ArrayList<>();
            for (int i = 0; i < months.length; i++) {
                float outcome = outcomesPerMonth.getOrDefault(months[i], 0f);
                float income = incomesPerMonth.getOrDefault(months[i], 0f);
                entriesStacked.add(new BarEntry(i, new float[]{outcome, income}));
            }

            BarDataSet setStacked = new BarDataSet(entriesStacked, "");
            setStacked.setColors(
                    getResources().getColor(R.color.red2, null),
                    getResources().getColor(R.color.green2, null)
            );
            setStacked.setStackLabels(new String[]{"Wydatki", "Wpływy"});
            setStacked.setValueTextColor(Color.BLACK);
            setStacked.setValueTextSize(12f);

            BarData data = new BarData(setStacked);
            data.setBarWidth(0.5f);

            final float totalIncomesFinal = totalIncomes;
            final float totalOutcomesFinal = totalOutcomes;
            final float avgSpendingFinal = avgSpending;

            requireActivity().runOnUiThread(() -> {
                textAvgSpending.setText(df.format(Math.abs(avgSpendingFinal)) + " PLN");
                textTotalIncomes.setText(df.format(totalIncomesFinal) + " PLN");
                textTotalOutcomes.setText(df.format(Math.abs(totalOutcomesFinal)) + " PLN");

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
                xAxis.setAxisMinimum(-0.5f);
                xAxis.setAxisMaximum(months.length - 0.5f);

                barChart.getAxisLeft().setAxisMinimum(0f);
                barChart.getAxisRight().setAxisMinimum(0f);
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
            });
        }).start();
    }

    private String getShortMonthName(String date) {
        if (date == null || date.length() < 7) return "";
        String monthNum = date.substring(5, 7);
        switch (monthNum) {
            case "03": return "Mar";
            case "04": return "Kwi";
            case "05": return "Maj";
            case "06": return "Cze";
            default: return "";
        }
    }
}