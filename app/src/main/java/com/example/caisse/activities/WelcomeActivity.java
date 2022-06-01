package com.example.caisse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.caisse.MainActivity;
import com.example.caisse.R;
import com.example.caisse.singletons.SharedAuthSingleton;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initialise();
        redirectUserIfNotAuthenticated();
    }

    /* Logout */
    private void handleLogout(View view) {
        new MaterialAlertDialogBuilder(WelcomeActivity.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout ?")
                .setPositiveButton("Confirm", (dialogInterface, i) -> SharedAuthSingleton.getInstance(getApplicationContext()).clear())
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                })
                .show();
    }

    /* Go to main activity if the user is not authenticated */
    private void redirectUserIfNotAuthenticated() {
        boolean isLoggedIn = SharedAuthSingleton.getInstance(WelcomeActivity.this).isLoggedIn();
        if (!isLoggedIn) {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /* Initialise layout */
    private void initialise() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bottom_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_fragment);
        BottomNavigationItemView btnLogout = findViewById(R.id.logoutItem);

        /* Initialize cart badge */
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.cartItem);
        badgeDrawable.setNumber(0);
        badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.color_red));

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        btnLogout.setOnClickListener(this::handleLogout);
    }

    /* Add cart badge number */
    public void createCartBadge(int number) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bottom_view);
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.cartItem);
        badgeDrawable.setNumber(number);
    }

}
