package com.example.webstoresoftwarepatternsca.View;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.webstoresoftwarepatternsca.Model.CartManager;
import com.example.webstoresoftwarepatternsca.Model.Product;
import com.example.webstoresoftwarepatternsca.R;
import com.squareup.picasso.Picasso;

public class ProductDetailFragment extends Fragment {

    private Product product;

    public ProductDetailFragment() {

    }

    public static ProductDetailFragment newInstance(Product product) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("product", product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable("product");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        // Initialize views and set product details
        ((TextView) view.findViewById(R.id.product_detail_title)).setText(product.getTitle());
        ((TextView) view.findViewById(R.id.product_detail_manufacturer)).setText(product.getManufacturer());
        ((TextView) view.findViewById(R.id.product_detail_price)).setText(String.format("€%.2f", product.getPrice()));
        ((TextView) view.findViewById(R.id.product_detail_category)).setText(product.getCategory());
        Picasso.get().load(product.getImageUrl()).into((ImageView) view.findViewById(R.id.product_detail_image));


        ImageView closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });
        view.findViewById(R.id.add_to_basket_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add product to basket here
                CartManager.getInstance().addItemToCart(product.getProductId(), 1);
                Toast.makeText(getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void closeFragment() {
        // Check if the fragment is attached to an activity before trying to remove it
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(ProductDetailFragment.this).commit();
        }
    }

}