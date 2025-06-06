package com.example.financeapp.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    public interface OnPaidClickListener {
        void onPaidClicked(BillReminder reminder);
    }
    public interface OnDeleteClickListener {
        void onDeleteClicked(BillReminder reminder);
    }

    private OnPaidClickListener paidClickListener;
    private OnDeleteClickListener deleteClickListener;

    public void setOnPaidClickListener(OnPaidClickListener listener) {
        this.paidClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

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

        holder.textDueDate.setText("Data: " + (reminder.dueDate != null ? reminder.dueDate : "Brak terminu"));
        holder.textDueDate.setTextColor(Color.parseColor("#222222"));

        holder.textAmount.setText("Kwota: " + String.format("%.2f PLN", reminder.amount));
        holder.textAmount.setTextColor(Color.parseColor("#222222"));

        holder.textStatus.setText("Status: " + (reminder.paid ? "Opłacone" : "Nieopłacony"));
        holder.textStatus.setTextColor(reminder.paid ? Color.parseColor("#388E3C") : Color.parseColor("#D32F2F"));
        holder.textDescription.setText("Opis: " + (reminder.message != null ? reminder.message : ""));

        holder.btnPaid.setVisibility(reminder.paid ? View.GONE : View.VISIBLE);
        holder.btnDelete.setVisibility(reminder.paid ? View.VISIBLE : View.GONE);

        holder.textTitle.setOnClickListener(v -> {
            if (expanded) expandedPositions.remove(position);
            else expandedPositions.add(position);
            notifyItemChanged(position);
        });

        holder.btnPaid.setOnClickListener(v -> {
            if (!reminder.paid && paidClickListener != null) {
                paidClickListener.onPaidClicked(reminder);
                notifyItemChanged(position);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClicked(reminder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDueDate, textAmount, textStatus, textDescription;
        LinearLayout layoutExpandable;
        Button btnPaid, btnDelete;

        ViewHolder(View v) {
            super(v);
            textTitle = v.findViewById(R.id.text_title);
            textDueDate = v.findViewById(R.id.text_due_date);
            textAmount = v.findViewById(R.id.text_amount);
            textStatus = v.findViewById(R.id.text_status);
            textDescription = v.findViewById(R.id.text_description);
            layoutExpandable = v.findViewById(R.id.layout_expandable);
            btnPaid = v.findViewById(R.id.btn_paid);
            btnDelete = v.findViewById(R.id.btn_delete);
        }
    }
}