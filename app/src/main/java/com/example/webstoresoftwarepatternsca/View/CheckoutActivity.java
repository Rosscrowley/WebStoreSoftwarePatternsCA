package com.example.webstoresoftwarepatternsca.View;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webstoresoftwarepatternsca.Model.CardDetail;
import com.example.webstoresoftwarepatternsca.Model.CartItem;
import com.example.webstoresoftwarepatternsca.Model.CartRepository;
import com.example.webstoresoftwarepatternsca.Model.Order;
import com.example.webstoresoftwarepatternsca.Model.OrderRepository;
import com.example.webstoresoftwarepatternsca.Model.ProductRepository;
import com.example.webstoresoftwarepatternsca.Model.ShippingAddress;
import com.example.webstoresoftwarepatternsca.Model.User;
import com.example.webstoresoftwarepatternsca.Model.UserRepository;
import com.example.webstoresoftwarepatternsca.Model.UserSessionManager;
import com.example.webstoresoftwarepatternsca.R;
import com.example.webstoresoftwarepatternsca.ViewModel.AddressValidationStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.CVVValidationStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.CardNumberValidationStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.DateValidationStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.GoldState;
import com.example.webstoresoftwarepatternsca.ViewModel.NoTierState;
import com.example.webstoresoftwarepatternsca.ViewModel.PlatinumState;
import com.example.webstoresoftwarepatternsca.ViewModel.PostalCodeValidationStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.SilverState;
import com.example.webstoresoftwarepatternsca.ViewModel.ValidationStrategy;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private EditText cardNumberEditText, expiryDateEditText, cvvEditText;
    private EditText addressLineEditText, cityEditText, postalCodeEditText;
    private Button confirmPurchaseButton;

    private UserRepository userRepository = new UserRepository();
    private User currentUser;

    private OrderRepository orderRepository = new OrderRepository();

    private CartRepository cartRepository = new CartRepository();
    private ProductRepository productRepository = new ProductRepository();

    private TextView totalPriceTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize views
        cardNumberEditText = findViewById(R.id.cardNumberEditText);
        expiryDateEditText = findViewById(R.id.expiryDateEditText);
        cvvEditText = findViewById(R.id.cvvEditText);
        addressLineEditText = findViewById(R.id.addressLineEditText);
        cityEditText = findViewById(R.id.cityEditText);
        postalCodeEditText = findViewById(R.id.postalCodeEditText);
        confirmPurchaseButton = findViewById(R.id.btnConfirmPurchase);
        confirmPurchaseButton = findViewById(R.id.btnConfirmPurchase);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        fetchAndPrepopulateUserDetails();
        confirmPurchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPurchase();
            }
        });
    }
    private void fetchAndPrepopulateUserDetails() {
        String currentUserId = UserSessionManager.getInstance().getFirebaseUserId();
        Toast.makeText(this, "Fetching user details...", Toast.LENGTH_SHORT).show();

        Log.d("CheckoutActivity", "Current user ID: " + currentUserId);
        userRepository.fetchUserById(currentUserId, new UserRepository.UserFetchListener() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    currentUser = user;
                    Log.d("CheckoutActivity", "User fetched successfully.");

                    double totalPrice = getIntent().getDoubleExtra("totalPrice", 0);
                    double discountedPrice = currentUser.applyDiscount(totalPrice);
                    totalPriceTextView.setText(String.format(Locale.US, "€%.2f", discountedPrice));

                    if (user.getCardDetail() == null) Log.d("CheckoutActivity", "Card detail is null.");
                    if (user.getShippingAddress() == null) Log.d("CheckoutActivity", "Shipping address is null.");
                    // Pre-populate card details if available
                    if (user.getCardDetail() != null) {
                        cardNumberEditText.setText(user.getCardDetail().getCardNumber());
                        expiryDateEditText.setText(user.getCardDetail().getExpiryDate());
                        cvvEditText.setText(user.getCardDetail().getCvv());
                    }

                    // Pre-populate shipping address if available
                    if (user.getShippingAddress() != null) {
                        addressLineEditText.setText(user.getShippingAddress().getAddress());
                        cityEditText.setText(user.getShippingAddress().getCity());
                        postalCodeEditText.setText(user.getShippingAddress().getPostalCode());
                    }

                    if (user.getCardDetail() == null && user.getShippingAddress() == null){
                        Log.d("CheckoutActivity", "Card details or shipping address not available. Showing Toast.");
                        Toast.makeText(CheckoutActivity.this, "Please enter Card and Shipping Address Details.", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Log.d("CheckoutActivity", "No user fetched or user is null.");

                }
            }
            @Override
            public void onError(DatabaseError error) {

            }
        });
    }

    private boolean validateFields() {
        ValidationStrategy addressStrategy = new AddressValidationStrategy();
        ValidationStrategy postalCodeStrategy = new PostalCodeValidationStrategy();
        ValidationStrategy cardNumberStrategy = new CardNumberValidationStrategy();
        ValidationStrategy cvvStrategy = new CVVValidationStrategy();
        ValidationStrategy dateStrategy = new DateValidationStrategy();

        if (!addressStrategy.validate(addressLineEditText.getText().toString())) {
            addressLineEditText.setError("Invalid Address Line");
            return false;
        }else if(!addressStrategy.validate(cityEditText.getText().toString())){
            cityEditText.setError("Invalid City");
            return false;
        } else if(!postalCodeStrategy.validate(postalCodeEditText.getText().toString())){
            postalCodeEditText.setError("Invalid postal code");
            return false;
        }else if(!cardNumberStrategy.validate(cardNumberEditText.getText().toString())){
            cardNumberEditText.setError("Invalid Card Number");
            return false;
        }else if(!dateStrategy.validate(expiryDateEditText.getText().toString())){
            expiryDateEditText.setError("Invalid Date please use dd/mm/yy Format");
            return false;
        }else if(!cvvStrategy.validate(cvvEditText.getText().toString())){
            cvvEditText.setError("Invalid CVV. 3 digit number on the back of card");
            return false;
        }
        return true;
    }

    private void confirmPurchase() {
        if (validateFields()) {
            fetchUserDetailsAndConfirmPurchase(UserSessionManager.getInstance().getFirebaseUserId());
        } else {
            Toast.makeText(this, "Please correct the errors before proceeding.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserDetailsAndConfirmPurchase(String userId) {
        userRepository.fetchUserById(userId, new UserRepository.UserFetchListener() {
            @Override
            public void onUserFetched(User user) {
                currentUser = user; // Update the current user with the fetched details
                if (currentUser != null) {
                    // Check and update loyalty state based on the total spent
                    updateLoyaltyState(currentUser);

                    double totalPrice = getIntent().getDoubleExtra("totalPrice", 0);
                    double discountedPrice = currentUser.applyDiscount(totalPrice);
                    Log.d("CheckoutActivity", "Total Price: " + totalPrice + " Discounted: " + discountedPrice);
                    processOrder(createOrderFromInput(discountedPrice));
                }
            }

            @Override
            public void onError(DatabaseError error) {
                Toast.makeText(CheckoutActivity.this, "Failed to fetch user details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void processOrder(Order order) {
        orderRepository.createOrder(order, new OrderRepository.OrderCreationCallback() {
            @Override
            public void onOrderCreated(Order createdOrder) {
                updateStockForOrderedItems(createdOrder.getCartItems());
                updateUserDetails(currentUser.getUserId());
                updateUserDetailsAndSpending(currentUser.getUserId(), createdOrder.getTotalAmount());
                cartRepository.clearUserCart(currentUser.getUserId());
            }

            @Override
            public void onOrderCreationFailed(Exception e) {
                Toast.makeText(CheckoutActivity.this, "Failed to place order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserDetailsAndSpending(String userId, double amountSpent) {
        userRepository.updateUserSpendingAndLoyalty(userId, amountSpent, new UserRepository.UserFetchListener() {
            @Override
            public void onUserFetched(User user) {
                Toast.makeText(CheckoutActivity.this, "Thank you for your purchase! Your loyalty status has been updated.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(DatabaseError error) {
                Log.e("CheckoutActivity", "Error updating user spending and loyalty: " + error.getMessage());
            }
        });
    }

    private void updateUserDetails(String userId) {
        CardDetail cardDetail = new CardDetail(
                cardNumberEditText.getText().toString().trim(),
                expiryDateEditText.getText().toString().trim(),
                cvvEditText.getText().toString().trim()
        );

        ShippingAddress shippingAddress = new ShippingAddress(
                addressLineEditText.getText().toString().trim(),
                cityEditText.getText().toString().trim(),
                postalCodeEditText.getText().toString().trim()
        );

        userRepository.updateUserCardDetails(userId, cardDetail);
        userRepository.updateUserShippingAddress(userId, shippingAddress);
    }

    private void updateStockForOrderedItems(List<CartItem> cartItems) {
        for (CartItem item : cartItems) {
            productRepository.updateProductStock(item.getProductId(), -item.getQuantity());
        }
    }

    private Order createOrderFromInput(double discountedPrice) {

        String cardNumber = cardNumberEditText.getText().toString().trim();
        String expiryDate = expiryDateEditText.getText().toString().trim();
        String cvv = cvvEditText.getText().toString().trim();
        String addressLine = addressLineEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String postalCode = postalCodeEditText.getText().toString().trim();

        List<CartItem> cartItems = getIntent().getParcelableArrayListExtra("cartItems");

        ShippingAddress sa = new ShippingAddress(addressLine, city, postalCode);
        CardDetail cd = new CardDetail(cardNumber, expiryDate, cvv);
        String currentUserId = UserSessionManager.getInstance().getFirebaseUserId();

        String orderId = FirebaseDatabase.getInstance().getReference().child("orders").push().getKey();
        Order order = new Order(orderId, currentUserId, cartItems, discountedPrice, cd, sa);

        return order;
    }

    private void updateLoyaltyState(User user) {
        double totalSpent = user.getTotalSpent();
        if (totalSpent >= 500 && !"Platinum".equals(user.getLoyaltyTier())) {
            user.setLoyaltyTier("Platinum");
        } else if (totalSpent >= 250 && !"Gold".equals(user.getLoyaltyTier())) {
            user.setLoyaltyTier("Gold");
        } else if (totalSpent >= 100 && !"Silver".equals(user.getLoyaltyTier())) {
            user.setLoyaltyTier("Silver");
        } else if (totalSpent < 100 && !"No Tier".equals(user.getLoyaltyTier())) {
            user.setLoyaltyTier("No Tier");
        }
        // Persist the changes to Firebase if necessary
        // userRepository.updateUserLoyaltyTier(userId, user.getLoyaltyTier());
    }

}

