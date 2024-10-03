package com.t1co.wanderlust;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, dashboardFragment).commit();
                return true;

            case R.id.berita:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, beritaFragment).commit();
                return true;

            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, profileFragment).commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    }

