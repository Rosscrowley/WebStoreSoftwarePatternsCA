package com.example.webstoresoftwarepatternsca.View;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webstoresoftwarepatternsca.Model.User;
import com.example.webstoresoftwarepatternsca.Model.UserRepository;
import com.example.webstoresoftwarepatternsca.R;
import com.example.webstoresoftwarepatternsca.ViewModel.UserCollection;
import com.example.webstoresoftwarepatternsca.ViewModel.UserIterator;
import com.example.webstoresoftwarepatternsca.ViewModel.UserList;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class CustomersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerAdapter adapter;
    private UserRepository userRepository = new UserRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomerAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Fetch users from the database
        fetchUsers();
    }

    private void fetchUsers() {
        userRepository.getAllUsers(new UserRepository.UserFetchListener() {
            @Override
            public void onUsersFetched(List<User> users) {

                UserCollection userCollection = new UserList(users);

                adapter.clearUsers();

                // Use the iterator to traverse the list of users to remove admins
                UserIterator iterator = userCollection.createIterator();
                while (iterator.hasNext()) {
                    User user = iterator.next();
                    adapter.addUser(user);
                }
            }

            @Override
            public void onUserFetched(User user) {
            }

            @Override
            public void onError(DatabaseError error) {
                Log.e("CustomerActivity", "Error fetching users: " + error.getMessage());
                Toast.makeText(CustomersActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
