package com.t1co.wanderlust.main.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.Galeri.GaleriPageActivity;
import com.t1co.wanderlust.main.Jadwal.JadwalCariActivity;
import com.t1co.wanderlust.main.KritikDanSaran.KritikDanSaranPageActivity;

public class DashboardPageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DashboardPageActivity";
    private ViewFlipper viewFlipper;
    private DrawerLayout drawerLayout;
    private ImageView menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_page);
        Log.d(TAG, "onCreate called");

        initViewFlipper();
        initButtons();

    }

    private void initViewFlipper() {
        viewFlipper = findViewById(R.id.viewFlipper);
        if (viewFlipper != null) {
            viewFlipper.setFlipInterval(1750);
            viewFlipper.setAutoStart(true);
            viewFlipper.startFlipping();
            Log.d(TAG, "ViewFlipper initialized");
        } else {
            Log.e(TAG, "ViewFlipper not found in layout");
        }
    }

    private void initButtons() {
        ImageButton historyButton = findViewById(R.id.button_image);
        ImageButton kritikButton = findViewById(R.id.button_kritik);
        ImageButton pemesananButton = findViewById(R.id.button_pemesanan);

        if (historyButton != null) {
            historyButton.setOnClickListener(this);
            Log.d(TAG, "History button listener set");
        } else {
            Log.e(TAG, "History button not found in layout");
        }

        if (kritikButton != null) {
            kritikButton.setOnClickListener(this);
            Log.d(TAG, "Kritik button listener set");
        } else {
            Log.e(TAG, "Kritik button not found in layout");
        }

        if (pemesananButton != null) {
            kritikButton.setOnClickListener(this);
            Log.d(TAG, "pemesanan button listener set");
        } else {
            Log.e(TAG, "pemesanan button not found in layout");
        }

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        Log.d(TAG, "onClick called for view with id: " + viewId);

        if (viewId == R.id.button_image) {
            Log.d(TAG, "History button clicked");
            startActivity(new Intent(this, GaleriPageActivity.class));
        } else if (viewId == R.id.button_kritik) {
            Log.d(TAG, "Kritik dan Saran button clicked");
            startActivity(new Intent(this, KritikDanSaranPageActivity.class));
        } else if (viewId == R.id.button_pemesanan) {
            Log.d(TAG, "pemesanan button clicked");
            startActivity(new Intent(this, JadwalCariActivity.class));
        } else {
            Log.w(TAG, "Unhandled click event for view with id: " + viewId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (viewFlipper != null) {
            viewFlipper.stopFlipping();
            Log.d(TAG, "ViewFlipper stopped in onPause");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewFlipper != null) {
            viewFlipper.startFlipping();
            Log.d(TAG, "ViewFlipper started in onResume");
        }
    }
}