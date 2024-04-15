package com.example.webstoresoftwarepatternsca.ViewModel;

import com.example.webstoresoftwarepatternsca.Model.User;

import java.util.List;

public class UserListIterator implements UserIterator {
    private List<User> users;
    private int position = 0;

    public UserListIterator(List<User> users) {
        this.users = users;
    }

    @Override
    public boolean hasNext() {
        while (position < users.size()) {
            if (!users.get(position).isAdmin()) {
                return true;
            }
            position++;
        }
        return false;
    }

    @Override
    public User next() {
        if (hasNext()) {
            return users.get(position++);
        }
        return null;
    }
}
