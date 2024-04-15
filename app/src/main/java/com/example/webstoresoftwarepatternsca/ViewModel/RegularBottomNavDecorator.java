package com.example.webstoresoftwarepatternsca.ViewModel;

import com.example.webstoresoftwarepatternsca.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RegularBottomNavDecorator implements BottomNavDecorator {
    @Override
    public void decorate(BottomNavigationView bottomNavigationView) {
        // Inflate regular menu
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.navigation_menu);
    }
}