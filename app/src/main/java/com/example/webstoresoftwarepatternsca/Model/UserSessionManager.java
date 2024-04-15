package com.example.webstoresoftwarepatternsca.Model;

import android.util.Log;

import com.google.firebase.database.DatabaseError;

import java.util.List;

public class UserSessionManager {
    private static UserSessionManager instance;
    private User currentUser;
    private UserRepository userRepository;
    private UserFetchListener fetchListener;

    private UserSessionManager() {
        userRepository = new UserRepository();
    }

    public static synchronized UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
        }
        return instance;
    }

    public void initializeSessionWithFirebaseUserId(String userId, UserFetchListener listener) {
        userRepository.fetchUserById(userId, new UserRepository.UserFetchListener() {
            @Override
            public void onUserFetched(User user) {
                currentUser = user;
                listener.onUserFetched(user);
            }
            @Override
            public void onError(DatabaseError error) {
                Log.e("UserSessionManager", "Error fetching user: " + error.getMessage());
                listener.onError(error);
            }

            @Override
            public void onUsersFetched(List<User> userList) {

            }
        });
    }
    public String getFirebaseUserId() {
        return currentUser != null ? currentUser.getUserId() : null;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public interface UserFetchListener {
        void onUserFetched(User user);
        void onError(DatabaseError error);
    }
}