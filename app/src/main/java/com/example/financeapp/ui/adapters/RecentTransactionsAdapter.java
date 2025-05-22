//package com.example.financeapp.ui.adapters;
//
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.DiffUtil;
//import androidx.recyclerview.widget.ListAdapter;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.room.Transaction;
//import com.example.financeapp.databinding.ItemTransactionBinding;
//
//public class RecentTransactionsAdapter extends ListAdapter<Transaction, RecentTransactionsAdapter.ViewHolder> {
//    public RecentTransactionsAdapter() {
//        super(DIFF_CALLBACK);
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        ItemTransactionBinding binding = ItemTransactionBinding.inflate(
//                LayoutInflater.from(parent.getContext()), parent, false);
//        return new ViewHolder(binding);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.bind(getItem(position));
//    }
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        private final ItemTransactionBinding binding;
//
//        ViewHolder(ItemTransactionBinding binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//        }
//
//        void bind(Transaction transaction) {
//            binding.tvRecipient.setText(transaction.getRecipient());
//            binding.tvAmount.setText(String.format("%.2f PLN", transaction.getAmount()));
//        }
//    }
//
//    private static final DiffUtil.ItemCallback<Transaction> DIFF_CALLBACK =
//            new DiffUtil.ItemCallback<Transaction>() {
//                @Override
//                public boolean areItemsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
//                    return oldItem.getId() == newItem.getId();
//                }
//
//                @Override
//                public boolean areContentsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
//                    return oldItem.equals(newItem);
//                }
//            };
//}
