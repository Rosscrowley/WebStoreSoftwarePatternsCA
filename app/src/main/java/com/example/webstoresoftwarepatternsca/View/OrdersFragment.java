package com.example.webstoresoftwarepatternsca.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webstoresoftwarepatternsca.Model.Order;
import com.example.webstoresoftwarepatternsca.Model.OrderRepository;
import com.example.webstoresoftwarepatternsca.R;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {
    private RecyclerView ordersRecyclerView;
    private OrdersAdapter adapter;
    private OrderRepository orderRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderRepository = new OrderRepository();
        adapter = new OrdersAdapter(new ArrayList<>());
        ordersRecyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ordersRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        ordersRecyclerView.addItemDecoration(dividerItemDecoration);

        if (getArguments() != null) {
            String userId = getArguments().getString("userId");
            loadOrders(userId);
        }

        return view;
    }

    public void loadOrders(String userId) {
        orderRepository.fetchOrdersForUser(userId, new OrderRepository.OrdersFetchListener() {
            @Override
            public void onOrdersFetched(List<Order> orders) {
                adapter.setOrders(orders);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(DatabaseError error) {
                Log.e("OrdersFragment", "Error fetching orders: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to fetch orders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}