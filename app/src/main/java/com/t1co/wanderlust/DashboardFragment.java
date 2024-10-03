package com.t1co.wanderlust;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DashboardFragment extends Fragment {

    private ViewFlipper viewFlipper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.activity_dashboard_page, container, false);

        // Initialize the ViewFlipper
        viewFlipper = view.findViewById(R.id.viewFlipper);
        viewFlipper.setFlipInterval(1750);
        viewFlipper.setAutoStart(true);
        viewFlipper.startFlipping(); // Start the flipping

        // Set up buttons if needed (you can add click listeners here)

        // Set click listener for historyButton if necessary

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (viewFlipper != null) {
            viewFlipper.stopFlipping(); // Stop flipping when paused
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewFlipper != null) {
            viewFlipper.startFlipping(); // Resume flipping when resumed
        }
    }
}