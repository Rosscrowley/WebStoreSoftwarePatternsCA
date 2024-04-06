package com.example.webstoresoftwarepatternsca.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webstoresoftwarepatternsca.Model.CartItem;
import com.example.webstoresoftwarepatternsca.Model.Product;
import com.example.webstoresoftwarepatternsca.R;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.Map;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private Map<String, Product> productMap;
    private OnItemRemoveListener onItemRemoveListener;

    public CartItemAdapter(Context context, List<CartItem> cartItems, Map<String, Product> productMap, OnItemRemoveListener onItemRemoveListener) {
        this.context = context;
        this.cartItems = cartItems;
        this.productMap = productMap;
        this.onItemRemoveListener = onItemRemoveListener;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_cart_product, parent, false);
        return new CartItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = productMap.get(cartItem.getProductId());

        if (product != null) {
            holder.productNameTextView.setText(product.getTitle());
            holder.productPriceTextView.setText(String.format("€%.2f", product.getPrice()));
            holder.productQuantityTextView.setText(String.format("Quantity: %d", cartItem.getQuantity()));
            Picasso.get().load(product.getImageUrl()).into(holder.productImageView);
        }

        holder.removeButton.setOnClickListener(v -> {
            if(onItemRemoveListener != null) {
                onItemRemoveListener.onItemRemoved(cartItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImageView;
        public TextView productNameTextView, productPriceTextView, productQuantityTextView;
        public Button removeButton;

        public CartItemViewHolder(View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImage);
            productNameTextView = itemView.findViewById(R.id.productName);
            productPriceTextView = itemView.findViewById(R.id.productPrice);
            productQuantityTextView = itemView.findViewById(R.id.productQuantity);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }

    public interface OnItemRemoveListener {
        void onItemRemoved(CartItem cartItem);
    }
}
