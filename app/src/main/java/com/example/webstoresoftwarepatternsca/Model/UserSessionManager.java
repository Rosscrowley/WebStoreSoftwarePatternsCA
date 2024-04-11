package com.example.webstoresoftwarepatternsca.Model;

public class UserSessionManager {
    private static UserSessionManager instance;
    private User currentUser;
    private UserRepository userRepository;

    private UserSessionManager() {
        userRepository = new UserRepository();
    }

    public static synchronized UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
        }
        return instance;
    }

    public void initializeSessionWithFirebaseUserId(String userId) {
        currentUser = new User(userId);
        userRepository.updateUserId(userId);
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
}
