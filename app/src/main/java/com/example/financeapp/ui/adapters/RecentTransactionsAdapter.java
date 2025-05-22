package com.example.financeapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financeapp.R;
import com.example.financeapp.ui.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class RecentTransactionsAdapter extends RecyclerView.Adapter<RecentTransactionsAdapter.ViewHolder> {

    private List<Transaction> transactions = new ArrayList<>();
    private OnTransactionClickListener listener;

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Transaction> transactions) {
        this.transactions = transactions != null ? transactions : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(transactions.get(position));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView recipient, amount;

        public ViewHolder(@NonNull View itemView, final OnTransactionClickListener listener) {
            super(itemView);
            recipient = itemView.findViewById(R.id.tvRecipient);
            amount = itemView.findViewById(R.id.tvAmount);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onTransactionClick((Transaction) v.getTag());
                }
            });
        }

        public void bind(Transaction transaction) {
            recipient.setText(transaction.getRecipient());
            amount.setText(String.format("%.2f PLN", transaction.getAmount()));
            itemView.setTag(transaction);
        }
    }
}