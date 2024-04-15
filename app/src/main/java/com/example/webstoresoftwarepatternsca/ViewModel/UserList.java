package com.example.webstoresoftwarepatternsca.ViewModel;

import com.example.webstoresoftwarepatternsca.Model.User;

import java.util.List;

public class UserList implements UserCollection {
    private List<User> users;

    public UserList(List<User> users) {
        this.users = users;
    }

    @Override
    public UserIterator createIterator() {
        return new UserListIterator(users);
    }
}