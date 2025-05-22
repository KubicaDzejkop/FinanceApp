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

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList = new ArrayList<>();

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_transaction_details, parent, false);
        return new TransactionViewHolder(itemView);
    }

    

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.textRecipient.setText(transaction.getRecipient());
        holder.textAmount.setText(String.format("%.2f zł", transaction.getAmount()));
        holder.textCategory.setText(transaction.getCategory());
        holder.textDate.setText(transaction.getDate());
        holder.textType.setText(transaction.getType().equals("income") ? "Przychód" : "Wydatek");
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactionList = transactions;
        notifyDataSetChanged();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView textRecipient, textAmount, textCategory, textDate, textType;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            textRecipient = itemView.findViewById(R.id.text_recipient);
            textAmount = itemView.findViewById(R.id.text_amount);
            textCategory = itemView.findViewById(R.id.text_operation_category);
            textDate = itemView.findViewById(R.id.text_date);
            textType = itemView.findViewById(R.id.text_operation_type);
        }
    }
}