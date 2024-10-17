package com.t1co.wanderlust.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.t1co.wanderlust.R;

public class DashboardNavigation extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    private BottomNavigationView bottomNavigation;
    private DashboardFragment dashboardFragment = new DashboardFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private BeritaFragment beritaFragment = new BeritaFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_dashboard);

        bottomNavigation = findViewById(R.id.bottomview);
        bottomNavigation.setOnItemSelectedListener(this);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        TextView txtUsernameTampil = findViewById(R.id.username_tampil);

        if (username != null && !username.isEmpty()) {
            txtUsernameTampil.setText("Hey " + username + ", Welcome to wanderlust!");
        } else {
            txtUsernameTampil.setText("Hey Welcome to wanderlust!");
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, dashboardFragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Menangani pemilihan item dari bottom navigation
        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, dashboardFragment).commit();
                clearWelcomeMessage();  // Hapus pesan saat berpindah ke fragment lain
                return true;

            case R.id.berita:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, beritaFragment).commit();
                clearWelcomeMessage();  // Hapus pesan saat berpindah ke fragment lain
                return true;

            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, profileFragment).commit();
                clearWelcomeMessage();  // Hapus pesan saat berpindah ke fragment lain
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Fungsi untuk menghapus pesan selamat datang
    private void clearWelcomeMessage() {
        TextView txtUsernameTampil = findViewById(R.id.username_tampil);
        txtUsernameTampil.setText("");
    }
}
