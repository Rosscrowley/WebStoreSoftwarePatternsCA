package com.example.webstoresoftwarepatternsca.View;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webstoresoftwarepatternsca.Model.Product;
import com.example.webstoresoftwarepatternsca.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private LayoutInflater inflater;

    // Listener for clicks
    private OnProductClickListener productClickListener;

    // Constructor
    public ProductAdapter(Context context, List<Product> productList) {
        this.inflater = LayoutInflater.from(context);
        this.productList = productList;
    }

    // Interface for click events
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    // Setter for the click listener
    public void setOnProductClickListener(OnProductClickListener listener) {
        this.productClickListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productNameTextView.setText(product.getTitle());
        holder.productPriceTextView.setText("€" + product.getPrice());
        Picasso.get().load(product.getImageUrl()).into(holder.productImageView); // I'm using Picasso to load the image

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productClickListener != null) {
                    productClickListener.onProductClick(product);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImageView;
        public TextView productNameTextView, productPriceTextView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
        }
    }
}
