package com.example.webstoresoftwarepatternsca.View;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webstoresoftwarepatternsca.Model.CardDetail;
import com.example.webstoresoftwarepatternsca.Model.CartItem;
import com.example.webstoresoftwarepatternsca.Model.CartRepository;
import com.example.webstoresoftwarepatternsca.Model.Order;
import com.example.webstoresoftwarepatternsca.Model.OrderRepository;
import com.example.webstoresoftwarepatternsca.Model.ShippingAddress;
import com.example.webstoresoftwarepatternsca.Model.User;
import com.example.webstoresoftwarepatternsca.Model.UserRepository;
import com.example.webstoresoftwarepatternsca.Model.UserSessionManager;
import com.example.webstoresoftwarepatternsca.R;
import com.example.webstoresoftwarepatternsca.ViewModel.AddressValidationStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.CVVValidationStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.CardNumberValidationStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.DateValidationStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.PostalCodeValidationStrategy;
import com.example.webstoresoftwarepatternsca.ViewModel.ValidationStrategy;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private EditText cardNumberEditText, expiryDateEditText, cvvEditText;
    private EditText addressLineEditText, cityEditText, postalCodeEditText;
    private Button confirmPurchaseButton;

    private UserRepository userRepository = new UserRepository();
    private User currentUser;

    private OrderRepository orderRepository = new OrderRepository();

    private CartRepository cartRepository = new CartRepository();
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
            Order order = createOrderFromInput();
            orderRepository.createOrder(order, new OrderRepository.OrderCreationCallback() {
                UserRepository userRepository = new UserRepository();
                @Override
                public void onOrderCreated(Order createdOrder) {
                    Toast.makeText(CheckoutActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                    String currentUserId = UserSessionManager.getInstance().getFirebaseUserId();
                    cartRepository.clearUserCart(currentUserId);
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

                    userRepository.updateUserCardDetails(currentUserId, cardDetail);
                    userRepository.updateUserShippingAddress(currentUserId, shippingAddress);
                }

                @Override
                public void onOrderCreationFailed(Exception e) {
                    Toast.makeText(CheckoutActivity.this, "Failed to place order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please correct the errors before proceeding.", Toast.LENGTH_SHORT).show();
        }
    }

    private Order createOrderFromInput() {

        String cardNumber = cardNumberEditText.getText().toString().trim();
        String expiryDate = expiryDateEditText.getText().toString().trim();
        String cvv = cvvEditText.getText().toString().trim();
        String addressLine = addressLineEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String postalCode = postalCodeEditText.getText().toString().trim();

        List<CartItem> cartItems = getIntent().getParcelableArrayListExtra("cartItems");
        double totalPrice = getIntent().getDoubleExtra("totalPrice", 0);

        ShippingAddress sa = new ShippingAddress(addressLine, city, postalCode);
        CardDetail cd = new CardDetail(cardNumber, expiryDate, cvv);
        String currentUserId = UserSessionManager.getInstance().getFirebaseUserId();

        String orderId = FirebaseDatabase.getInstance().getReference().child("orders").push().getKey();
        Order order = new Order(orderId, currentUserId, cartItems, totalPrice, cd, sa);

        return order;
    }

}

