package com.t1co.wanderlust;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.ViewFlipper;

public class DashboardPageActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard_page);

        viewFlipper = findViewById(R.id.viewFlipper);

        // Set interval untuk flip otomatis (dalam milidetik)
        viewFlipper.setFlipInterval(1750); // Setiap 1.75 detik
        viewFlipper.setAutoStart(true); // Mulai flip otomatis
        viewFlipper.startFlipping();

        // Load default fragment
    }




    @Override
    protected void onPause() {
        super.onPause();
        viewFlipper.stopFlipping(); // Hentikan flip saat aktivitas tidak aktif
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewFlipper.startFlipping(); // Mulai flip lagi saat aktivitas dilanjutkan
    }
}