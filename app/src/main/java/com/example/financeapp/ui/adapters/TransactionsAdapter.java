package com.example.financeapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financeapp.R;
import com.example.financeapp.ui.models.Transaction;
import com.example.financeapp.ui.models.TransactionListItem;

import java.util.ArrayList;
import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TransactionListItem> items = new ArrayList<>();
    private OnTransactionClickListener listener;

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<TransactionListItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TransactionListItem.TYPE_DATE_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_transaction_date_header, parent, false);
            return new DateHeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_transaction, parent, false);
            return new TransactionViewHolder(v, listener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TransactionListItem item = items.get(position);
        if (item.getType() == TransactionListItem.TYPE_DATE_HEADER) {
            ((DateHeaderViewHolder) holder).bind(item.getDate());
        } else if (item.getType() == TransactionListItem.TYPE_TRANSACTION) {
            ((TransactionViewHolder) holder).bind(item.getTransaction());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateHeader;
        DateHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateHeader = itemView.findViewById(R.id.tvDateHeader);
        }
        void bind(String date) {
            tvDateHeader.setText(date);
        }
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView recipient, amount;

        TransactionViewHolder(@NonNull View itemView, final OnTransactionClickListener listener) {
            super(itemView);
            recipient = itemView.findViewById(R.id.tvRecipient);
            amount = itemView.findViewById(R.id.tvAmount);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onTransactionClick((Transaction) v.getTag());
                }
            });
        }

        void bind(Transaction transaction) {
            recipient.setText(transaction.getRecipient());
            amount.setText(String.format("%.2f PLN", transaction.getAmount()));
            if (transaction.getAmount() < 0) {
                amount.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            } else {
                amount.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            }
            itemView.setTag(transaction);
        }
    }
}
