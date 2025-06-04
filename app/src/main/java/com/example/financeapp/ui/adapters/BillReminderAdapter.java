package com.example.financeapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financeapp.R;
import com.example.financeapp.ui.models.BillReminder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BillReminderAdapter extends RecyclerView.Adapter<BillReminderAdapter.ViewHolder> {
    private List<BillReminder> reminders = new ArrayList<>();
    private Set<Integer> expandedPositions = new HashSet<>();

    public void setData(List<BillReminder> data) {
        this.reminders = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BillReminder reminder = reminders.get(position);
        holder.textTitle.setText(reminder.title);

        boolean expanded = expandedPositions.contains(position);
        holder.layoutExpandable.setVisibility(expanded ? View.VISIBLE : View.GONE);

        holder.textDescription.setText(reminder.message);
        holder.textDueDate.setText("Termin: " + (reminder.dueDate != null ? reminder.dueDate : "nie ustawiono"));

        holder.textTitle.setOnClickListener(v -> {
            if (expanded) expandedPositions.remove(position);
            else expandedPositions.add(position);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDescription, textDueDate;
        LinearLayout layoutExpandable;

        ViewHolder(View v) {
            super(v);
            textTitle = v.findViewById(R.id.text_title);
            textDescription = v.findViewById(R.id.text_description);
            textDueDate = v.findViewById(R.id.text_due_date);
            layoutExpandable = v.findViewById(R.id.layout_expandable);
        }
    }
}