package com.example.webstoresoftwarepatternsca.View;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webstoresoftwarepatternsca.Model.CartManager;
import com.example.webstoresoftwarepatternsca.Model.Comment;
import com.example.webstoresoftwarepatternsca.Model.CommentsRepository;
import com.example.webstoresoftwarepatternsca.Model.Product;
import com.example.webstoresoftwarepatternsca.Model.ProductRepository;
import com.example.webstoresoftwarepatternsca.Model.User;
import com.example.webstoresoftwarepatternsca.Model.UserSessionManager;
import com.example.webstoresoftwarepatternsca.R;
import com.google.firebase.database.DatabaseError;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailFragment extends Fragment {

    private Product product;

    private RecyclerView commentsRecyclerView;
    private CommentsAdapter commentsAdapter;
    private CommentsRepository commentsRepository;

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

        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize views and set product details
        ((TextView) view.findViewById(R.id.product_detail_title)).setText(product.getTitle());
        ((TextView) view.findViewById(R.id.product_detail_manufacturer)).setText(product.getManufacturer());
        ((TextView) view.findViewById(R.id.product_detail_price)).setText(String.format("€%.2f", product.getPrice()));
        ((TextView) view.findViewById(R.id.product_detail_category)).setText(product.getCategory());
        Picasso.get().load(product.getImageUrl()).into((ImageView) view.findViewById(R.id.product_detail_image));

        if (checkIfUserIsAdmin()) {
            // Disable adding to basket and leaving comments
            view.findViewById(R.id.add_to_basket_button).setVisibility(View.GONE);
            view.findViewById(R.id.leaveCommentTextView).setVisibility(View.GONE);
        } else {
            // Allow interactions for non-admin users
            view.findViewById(R.id.add_to_basket_button).setOnClickListener(v -> checkStockBeforeAddingToBasket(product));
            view.findViewById(R.id.leaveCommentTextView).setOnClickListener(v -> showCommentDialog());
        }

        commentsAdapter = new CommentsAdapter(new ArrayList<>());
        commentsRecyclerView.setAdapter(commentsAdapter);

        TextView leaveCommentTextView = view.findViewById(R.id.leaveCommentTextView);
        leaveCommentTextView.setOnClickListener(v -> showCommentDialog());

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        commentsRecyclerView.addItemDecoration(itemDecoration);

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
                checkStockBeforeAddingToBasket(product);
            }
        });

        loadComments();
        return view;
    }

    private void closeFragment() {
        // Check if the fragment is attached to an activity before trying to remove it
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(ProductDetailFragment.this).commit();
        }
    }

    private void loadComments() {
        commentsRepository = new CommentsRepository(product.getProductId());
        CommentsRepository.DataStatus dataStatus = new CommentsRepository.DataStatus() {
            @Override
            public void DataIsLoaded(List<Comment> comments) {
                commentsAdapter.setComments(comments);
                commentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void DataLoadFailed(DatabaseError databaseError) {
                Log.e("CommentsFragment", "Failed to load comments: " + databaseError.getMessage());
            }

            @Override
            public void AverageRatingLoaded(float averageRating) {

            }
        };
        commentsRepository.getComments(dataStatus);
    }

    private void showCommentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Leave a Comment and Rating");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.comment_dialog, null);
        EditText commentInput = dialogView.findViewById(R.id.commentEditText);
        RatingBar ratingInput = dialogView.findViewById(R.id.ratingBar);
        builder.setView(dialogView);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String commentText = commentInput.getText().toString();
            float rating = ratingInput.getRating();
            submitCommentAndRating(commentText, rating);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void submitCommentAndRating(String commentText, float rating) {
        if (product == null) {
            Toast.makeText(getContext(), "Product data is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = UserSessionManager.getInstance().getFirebaseUserId();
        // Generate a unique ID for the comment
        String commentId = commentsRepository.getCommentsRef().push().getKey();
        long timestamp = System.currentTimeMillis();

        Comment commentObject = new Comment(
                commentId,
                userId,
                commentText,
                timestamp,
                product.getProductId(),
                rating
        );

        commentsRepository.addComment(commentObject).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Comment and rating added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add comment: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkStockBeforeAddingToBasket(Product product) {
        ProductRepository productRepository = new ProductRepository();
        productRepository.getProductStock(product.getProductId(), new ProductRepository.DataStatus() {
            @Override
            public void StockLevelLoaded(int stockLevel) {
                if (stockLevel > 0) {
                    CartManager.getInstance().addItemToCart(product.getProductId(), 1);
                    Toast.makeText(getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Out of stock", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void DataLoadFailed(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load product details", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void DataIsLoaded(List<Product> products, List<String> keys) {}
            @Override
            public void DataIsInserted() {}
            @Override
            public void DataIsUpdated() {}
            @Override
            public void DataIsDeleted() {}
        });
    }

    private boolean checkIfUserIsAdmin() {
        UserSessionManager sessionManager = UserSessionManager.getInstance();

        if (sessionManager.getCurrentUser() != null) {
            User user = sessionManager.getCurrentUser();
            boolean isAdmin = user.isAdmin();
            return isAdmin;
        } else {
            Log.d("UserAdminCheck", "No user found in session manager");
        }

        return false;
    }

}