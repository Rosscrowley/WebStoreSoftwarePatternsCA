package com.example.webstoresoftwarepatternsca.View;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webstoresoftwarepatternsca.Model.Product;
import com.example.webstoresoftwarepatternsca.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ProductAdapter extends BaseAdapter<Product, ProductAdapter.ProductViewHolder> {

    private OnProductClickListener productClickListener;

    public ProductAdapter(Context context, List<Product> productList) {
        super(context, productList);
    }

    @Override
    protected boolean itemMatches(Product product, String filterPattern) {
        return product.getTitle().toLowerCase().contains(filterPattern);
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.productClickListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    protected void bindItem(ProductViewHolder holder, Product product) {
        holder.productNameTextView.setText(product.getTitle());
        holder.productPriceTextView.setText("€" + product.getPrice());
        Picasso.get().load(product.getImageUrl()).into(holder.productImageView);
        holder.productRatingBar.setRating(product.getAverageRating());


        holder.itemView.setOnClickListener(v -> {
            if (productClickListener != null) {
                productClickListener.onProductClick(product);
            }
        });
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImageView;
        public TextView productNameTextView, productPriceTextView;
        public RatingBar productRatingBar;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productRatingBar = itemView.findViewById(R.id.ratingBar2);
        }
    }
}
