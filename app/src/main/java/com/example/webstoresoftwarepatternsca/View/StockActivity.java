package com.example.webstoresoftwarepatternsca.View;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webstoresoftwarepatternsca.Model.Product;
import com.example.webstoresoftwarepatternsca.Model.ProductRepository;
import com.example.webstoresoftwarepatternsca.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class StockActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StockAdapter stockAdapter;
    private ProductRepository productRepository;
    private SearchView searchView;
    private BottomNavigationView navView;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        stockAdapter = new StockAdapter(this, new ArrayList<>(), new StockAdapter.OnStockUpdateListener() {
            @Override
            public void onStockUpdate(Product product, int newStockAmount) {
                updateProductStock(product, newStockAmount);
            }
        });
        recyclerView.setAdapter(stockAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(item -> navigateTo(item.getItemId()));


        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                stockAdapter.getFilter().filter(newText);
                return true;
            }
        });


        Button addProductButton = findViewById(R.id.btnAddNewProduct);
        addProductButton.setOnClickListener(v -> showAddProductDialog());

        productRepository = new ProductRepository();
        fetchProducts();
    }

    private boolean navigateTo(int itemId) {
        Intent intent = null;
        if (itemId == R.id.menu_item_home) {
            intent = new Intent(StockActivity.this, MainActivity.class);
        } else if (itemId == R.id.menu_item_customers) {
            intent = new Intent(StockActivity.this, CustomersActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
            return true;
        }
        return false;
    }

    private void fetchProducts() {
        productRepository.getProducts(new ProductRepository.DataStatus() {
            @Override
            public void DataIsLoaded(List<Product> products, List<String> keys) {
                stockAdapter.setItems(products);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }

            @Override
            public void DataLoadFailed(DatabaseError databaseError) {
                Toast.makeText(StockActivity.this, "Failed to load products: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void StockLevelLoaded(int stockLevel) {

            }
        });
    }

    private void updateProductStock(Product product, int newStockAmount) {
        productRepository.updateProductStock(product.getProductId(), newStockAmount);
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Product");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText inputTitle = viewInflated.findViewById(R.id.edit_product_title);
        final EditText inputPrice = viewInflated.findViewById(R.id.edit_product_price);
        final EditText inputStock = viewInflated.findViewById(R.id.edit_product_stock);
        final EditText inputManu = viewInflated.findViewById(R.id.edit_product_manufacturer);
        final EditText inputCategory = viewInflated.findViewById(R.id.edit_product_category);
        imageView = viewInflated.findViewById(R.id.image_product_preview);
        Button selectImageButton = viewInflated.findViewById(R.id.button_select_image);

        selectImageButton.setOnClickListener(v -> openFileChooser());

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            String title = inputTitle.getText().toString();
            double price = Double.parseDouble(inputPrice.getText().toString());
            int stock = Integer.parseInt(inputStock.getText().toString());
            String manu = inputManu.getText().toString();
            String cat = inputCategory.getText().toString();

            String productId = FirebaseDatabase.getInstance().getReference("products").push().getKey();
            Product newProduct = new Product(productId, title, price, null, 0);
            newProduct.setManufacturer(manu);
            newProduct.setCategory(cat);
            newProduct.setStock(stock);

            if (imageUri != null) {
                uploadFile(imageUri, newProduct);
            } else {
                productRepository.addProduct(newProduct);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);
        }
    }

    private void uploadFile(Uri imageUri, Product product) {
        StorageReference fileReference = FirebaseStorage.getInstance().getReference("uploads").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
            product.setImageUrl(uri.toString());
            productRepository.addProduct(product);
        })).addOnFailureListener(e -> Toast.makeText(StockActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}