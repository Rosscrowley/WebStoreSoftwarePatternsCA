package com.example.webstoresoftwarepatternsca.View;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webstoresoftwarepatternsca.Model.Product;
import com.example.webstoresoftwarepatternsca.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class StockAdapter extends BaseAdapter<Product, StockAdapter.StockViewHolder> {

    public interface OnStockUpdateListener {
        void onStockUpdate(Product product, int newStockAmount);
    }

    private OnStockUpdateListener stockUpdateListener;

    public StockAdapter(Context context, List<Product> products, OnStockUpdateListener listener) {
        super(context, products);
        this.stockUpdateListener = listener;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_product, parent, false);
        return new StockViewHolder(itemView);
    }

    @Override
    protected boolean itemMatches(Product product, String filterPattern) {
        boolean matches = product.getTitle().toLowerCase().contains(filterPattern.toLowerCase());
        Log.d("Filtering", "Testing Item: " + product.getTitle() + " for pattern " + filterPattern + " Result: " + matches);
        return matches;
    }
    @Override
    protected void bindItem(StockViewHolder holder, Product product) {
        holder.textTitle.setText(product.getTitle());
        holder.textPrice.setText(String.format(Locale.US, "€%.2f", product.getPrice()));
        holder.textStock.setText(String.format(Locale.US, "Stock: %d", product.getStock()));
        Picasso.get().load(product.getImageUrl()).into(holder.imageProduct);

        holder.imageAddStock.setOnClickListener(view -> {
            showStockUpdateDialog(product);
        });
    }

    private void showStockUpdateDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Stock");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            int newStock = Integer.parseInt(input.getText().toString());
            stockUpdateListener.onStockUpdate(product, newStock);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public static class StockViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct, imageAddStock;
        TextView textTitle, textPrice, textStock;

        public StockViewHolder(View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.image_product);
            imageAddStock = itemView.findViewById(R.id.image_add_stock);
            textTitle = itemView.findViewById(R.id.text_title);
            textPrice = itemView.findViewById(R.id.text_price);
            textStock = itemView.findViewById(R.id.text_stock);
        }
    }
}
