package com.example.webstoresoftwarepatternsca.View;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webstoresoftwarepatternsca.Model.CardDetail;
import com.example.webstoresoftwarepatternsca.Model.Order;
import com.example.webstoresoftwarepatternsca.Model.ShippingAddress;
import com.example.webstoresoftwarepatternsca.R;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {
    private List<Order> orderList;

    public OrdersAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    public void setOrders(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.textViewOrderId.setText("Order ID: " + order.getOrderId());
        holder.textViewTotalAmount.setText("Total: $" + order.getTotalAmount());
        ShippingAddress shippingAddress = order.getShippingAddress();
        holder.textViewShippingAddress.setText("Address: " + shippingAddress.getAddress() + ", " + shippingAddress.getCity() + ", " + shippingAddress.getPostalCode());
        CardDetail cardDetail = order.getCardDetail();
        String maskedCardNumber = "****" + cardDetail.getCardNumber().substring(cardDetail.getCardNumber().length() - 4);
        holder.textViewCardDetails.setText("Card: " + maskedCardNumber + ", Expiry: " + cardDetail.getExpiryDate());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOrderId, textViewTotalAmount, textViewShippingAddress, textViewCardDetails;

        OrderViewHolder(View view) {
            super(view);
            textViewOrderId = view.findViewById(R.id.textViewOrderId);
            textViewTotalAmount = view.findViewById(R.id.textViewTotalAmount);
            textViewShippingAddress = view.findViewById(R.id.textViewShippingAddress);
            textViewCardDetails = view.findViewById(R.id.textViewCardDetails);
        }
    }
}

