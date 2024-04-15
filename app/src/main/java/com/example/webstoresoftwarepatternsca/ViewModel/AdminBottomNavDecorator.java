package com.example.webstoresoftwarepatternsca.ViewModel;

import com.example.webstoresoftwarepatternsca.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminBottomNavDecorator implements BottomNavDecorator {
    @Override
    public void decorate(BottomNavigationView bottomNavigationView) {
        // Inflate admin menu
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.admin_navigation_menu);
    }
}