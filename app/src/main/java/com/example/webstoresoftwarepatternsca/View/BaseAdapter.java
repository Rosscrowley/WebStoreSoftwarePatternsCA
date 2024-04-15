package com.example.webstoresoftwarepatternsca.View;

import android.content.Context;
import android.util.Log;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected List<T> items;
    protected List<T> itemsFull;
    protected Context context;

    public BaseAdapter(Context context, List<T> items) {
        this.context = context;
        this.items = items;
        this.itemsFull = new ArrayList<>(items);
    }

    public Filter getFilter() {
        return itemFilter;
    }

    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<T> filteredList = new ArrayList<>();
            Log.d("Filtering", "Start filtering for: " + constraint);

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(itemsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (T item : itemsFull) {
                    if (itemMatches(item, filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            Log.d("Filtering", "Filtered Count: " + filteredList.size());
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            items.clear();
            items.addAll((List) results.values);
            Log.d("Filtering", "PublishResults Count: " + ((List) results.values).size());

            notifyDataSetChanged();
        }
    };
    protected abstract boolean itemMatches(T item, String filterPattern);

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        T item = items.get(position);
        bindItem(holder, item);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void setItems(List<T> items) {
        this.items = new ArrayList<>(items);
        this.itemsFull = new ArrayList<>(items);
        notifyDataSetChanged();
        Log.d("Filtering", "Items Set, Count: " + items.size());
    }

    protected abstract void bindItem(VH holder, T item);
}
