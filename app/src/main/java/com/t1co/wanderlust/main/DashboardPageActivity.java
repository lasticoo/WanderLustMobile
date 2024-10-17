package com.t1co.wanderlust.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ViewFlipper;

import com.t1co.wanderlust.R;

public class DashboardPageActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_page);

        // Initialize the ViewFlipper
        viewFlipper = findViewById(R.id.viewFlipper);
        viewFlipper.setFlipInterval(1750);
        viewFlipper.setAutoStart(true);
        viewFlipper.startFlipping(); // Start the flipping

    } // Closing brace for onCreate

    @Override
    protected void onPause() {
        super.onPause();
        viewFlipper.stopFlipping(); // Stop flipping when paused
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewFlipper.startFlipping(); // Resume flipping when resumed
    }
}