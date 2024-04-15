package com.example.webstoresoftwarepatternsca.View;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.webstoresoftwarepatternsca.Model.User;
import com.example.webstoresoftwarepatternsca.R;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<User> userList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }
    public CustomerAdapter(List<User> userList, OnItemClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public void addUser(User user) {
        userList.add(user);
        notifyItemInserted(userList.size() - 1);
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(user));
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void clearUsers() {
        int size = userList.size();
        userList.clear();
        notifyItemRangeRemoved(0, size);
    }
    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView customerIdTextView, emailTextView, totalSpentTextView, loyaltyTierTextView;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            customerIdTextView = itemView.findViewById(R.id.text_customer_id);
            emailTextView = itemView.findViewById(R.id.text_email);
            totalSpentTextView = itemView.findViewById(R.id.text_total_spent);
            loyaltyTierTextView = itemView.findViewById(R.id.text_loyalty_tier);
        }

        public void bind(User user) {
            customerIdTextView.setText(user.getUserId());
            emailTextView.setText(user.getEmail());
            totalSpentTextView.setText(String.valueOf(user.getTotalSpent()));
            loyaltyTierTextView.setText(user.getLoyaltyTier());
        }
    }
}