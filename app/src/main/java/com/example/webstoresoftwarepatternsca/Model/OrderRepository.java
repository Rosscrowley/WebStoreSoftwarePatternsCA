package com.example.webstoresoftwarepatternsca.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
}