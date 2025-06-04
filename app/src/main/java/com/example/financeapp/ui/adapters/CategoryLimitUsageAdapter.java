package com.example.financeapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financeapp.R;
import java.util.List;

public class CategoryLimitUsageAdapter extends RecyclerView.Adapter<CategoryLimitUsageAdapter.ViewHolder> {
    public static class RowData {
        public String category;
        public double limit;
        public double percent;
        public RowData(String category, double limit, double percent) {
            this.category = category;
            this.limit = limit;
            this.percent = percent;
        }
    }

    private List<RowData> data;

    public CategoryLimitUsageAdapter(List<RowData> data) {
        this.data = data;
    }

    public void setData(List<RowData> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_limit_usage, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RowData row = data.get(position);
        holder.tvCategoryName.setText(row.category);
        holder.tvLimit.setText(row.limit > 0 ? String.format("%.2f zł", row.limit) : "-");
        holder.tvPercent.setText(row.limit > 0 ? String.format("%.0f%%", row.percent) : "-");
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvLimit, tvPercent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvLimit = itemView.findViewById(R.id.tvLimit);
            tvPercent = itemView.findViewById(R.id.tvPercent);
        }
    }
}