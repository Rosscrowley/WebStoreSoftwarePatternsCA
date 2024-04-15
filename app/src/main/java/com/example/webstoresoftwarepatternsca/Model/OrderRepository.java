package com.example.webstoresoftwarepatternsca.Model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private DatabaseReference ordersRef;

    public OrderRepository() {
        ordersRef = FirebaseDatabase.getInstance("https://softwarepatternsca-f1030-default-rtdb.europe-west1.firebasedatabase.app").getReference("orders");
    }

    public void createOrder(Order order, final OrderCreationCallback callback) {
        String orderId = ordersRef.push().getKey();
        order.setOrderId(orderId);
        ordersRef.child(orderId).setValue(order)
                .addOnSuccessListener(aVoid -> callback.onOrderCreated(order))
                .addOnFailureListener(e -> callback.onOrderCreationFailed(e));
    }

    public interface OrderCreationCallback {
        void onOrderCreated(Order order);
        void onOrderCreationFailed(Exception e);
    }

    public void fetchOrdersForUser(String userId, OrdersFetchListener listener) {
        ordersRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Order> orders = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        orders.add(order);
                    }
                }
                listener.onOrdersFetched(orders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onError(databaseError);
            }
        });
    }

    public interface OrdersFetchListener {
        void onOrdersFetched(List<Order> orders);
        void onError(DatabaseError error);
    }
}